package com.dabom.video.service.s3;

import com.dabom.video.model.Video;
import com.dabom.video.model.VideoStatus;
import com.dabom.video.repository.VideoRepository;
import com.dabom.video.service.utils.FfmpegEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoS3EncodingService {

    private static final String TEMP_DOWNLOAD_DIR = "temp/";
    private static final String VIDEO_HLS_PATH = "videos/hls/";

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private final VideoRepository videoRepository;
    private final FfmpegEncoder ffmpegEncoder;
    private final S3Client s3Client;

    @Async("ffmpegExecutor")
    public void encode(Integer videoIdx) {
        log.info("===== Video S3 인코딩 시작 - videoIdx: {} =====", videoIdx);

        try {
            // 1. Video Entity 조회
            Video video = getVideoEntity(videoIdx);

            // 2. S3에서 원본 파일 다운로드
            String downloadPath = downloadOriginalFromS3(video);

            // 3. ffmpeg 인코딩 수행
            String encodingDir = ffmpegEncoding(downloadPath);

            // 4. 인코딩된 파일들을 S3에 업로드
            String s3EncodedPath = uploadEncodedFilesToS3(encodingDir);

            // 5. Video Entity 업데이트
            updateVideoEntity(video, s3EncodedPath);

            // 6. 로컬 임시 파일 정리
            cleanupLocalFiles(downloadPath, encodingDir);

            log.info("===== Video S3 인코딩 완료 ===== - videoIdx: {} ", videoIdx);

        } catch (Exception e) {
            log.error("Video S3 인코딩 실패 - videoIdx: {}", videoIdx, e);
            updateVideoEntityToFailed(videoIdx);
        }
    }

    // ===== 1단계: Video Entity 조회 =====
    private Video getVideoEntity(Integer videoIdx) {
        log.info("##### 1. Video Entity 조회 #####");
        return videoRepository.findById(videoIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 비디오입니다: " + videoIdx));
    }

    // ===== 2단계: S3에서 원본 파일 다운로드 =====
    private String downloadOriginalFromS3(Video video) throws IOException {
        String s3Key = video.getOriginalPath();
        log.info("##### 2. S3 원본 영상 다운로드 ##### -> {} ", s3Key);

        try {
            // 1. S3 파일 존재 여부 확인
            verifyS3FileExists(s3Key);

            // 2. 로컬 임시 파일 경로 생성
            String todayUploadPath = createTodayUploadPath(s3Key);

            // 3. S3에서 파일 다운로드
            downloadFileS3(s3Key, todayUploadPath);

            log.info("S3 파일 다운로드 완료: {} -> {}", s3Key, todayUploadPath);
            return todayUploadPath;

        } catch (Exception e) {
            log.error("S3 파일 다운로드 실패: {}", s3Key, e);
            throw new IOException("S3 파일 다운로드 실패: " + s3Key, e);
        }
    }

    private void verifyS3FileExists(String s3Key) {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();
            s3Client.headObject(headRequest);
        } catch (NoSuchKeyException e) {
            throw new IllegalArgumentException("S3에 파일이 존재하지 않습니다: " + s3Key);
        } catch (Exception e) {
            throw new RuntimeException("S3 파일 확인 중 오류 발생: " + s3Key, e);
        }
    }

    private String createTodayUploadPath(String s3Key) throws IOException {
        String todayDir = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = Paths.get(s3Key).getFileName().toString();

        Path localPath = Paths.get(TEMP_DOWNLOAD_DIR, todayDir, fileName);
        Files.createDirectories(localPath.getParent());

        return localPath.toString();
    }



    private void downloadFileS3(String s3Key, String downloadPath) throws IOException {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        try (InputStream s3InputStream = s3Client.getObject(getRequest);
             FileOutputStream fileOutputStream = new FileOutputStream(downloadPath);
             BufferedInputStream bis = new BufferedInputStream(s3InputStream);
             BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalBytes = 0;

            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }

            log.info("파일 다운로드 완료 - 총 크기: {} bytes", totalBytes);

        } catch (Exception e) {
            // 다운로드 실패 시 부분 파일 삭제
            try {
                Files.deleteIfExists(Paths.get(downloadPath));
            } catch (IOException deleteException) {
                log.warn("부분 다운로드 파일 삭제 실패: {}", downloadPath, deleteException);
            }
            throw new IOException("S3 파일 다운로드 중 오류 발생", e);
        }
    }

    // ===== 3단계: ffmpeg 인코딩 수행 =====
    private String ffmpegEncoding(String downloadPath) throws IOException, InterruptedException {
        log.info("##### 3. ffmpeg 인코딩 시작 ##### -> {} ", downloadPath);
        return ffmpegEncoder.encode(downloadPath);
    }

    // ===== 4단계: 인코딩된 파일들 S3 업로드 =====
    private String uploadEncodedFilesToS3(String localEncodingDir) throws IOException {
        log.info("##### 4. 인코딩 파일 S3에 업로드 ##### -> {} ", localEncodingDir);

        try {
            // 1. S3 업로드 경로 생성
            String s3PrefixPath = generateS3UploadPathPrefix();

            // 2. 로컬 디렉터리의 모든 파일을 S3에 업로드
            uploadDirectoryToS3(localEncodingDir, s3PrefixPath);

            // 3. 메인 플레이리스트(index.m3u8) S3 경로 반환
            return s3PrefixPath + "index.m3u8";

        } catch (Exception e) {
            log.error("S3 업로드 실패: {}", localEncodingDir, e);
            throw new IOException("S3 업로드 실패", e);
        }
    }

    private String generateS3UploadPathPrefix() {
        String todayPath = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString();

        return  VIDEO_HLS_PATH + todayPath + "/" + uuid + "/";
    }

    private void uploadDirectoryToS3(String localDir, String s3PrefixPath) throws IOException {
        Path localPath = Paths.get(localDir);

        try (Stream<Path> files = Files.walk(localPath)) {
            files.filter(Files::isRegularFile)
                    .forEach(file -> uploadSingleFileToS3(file, s3PrefixPath));
        }
    }

    private void uploadSingleFileToS3(Path localFile, String s3Prefix) {
        try {
            String fileName = localFile.getFileName().toString();
            String s3Key = s3Prefix + fileName;

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(getContentType(fileName))
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromFile(localFile));
        } catch (Exception e) {
            log.error("S3 개별 파일 업로드 실패: {}", localFile, e);
            throw new RuntimeException("S3 업로드 실패: " + localFile, e);
        }
    }

    private String getContentType(String fileName) {
        if (fileName.endsWith(".m3u8")) {
            return "application/vnd.apple.mpegurl";
        } else if (fileName.endsWith(".ts")) {
            return "video/mp2t";
        }
        return "application/octet-stream";
    }

    // ===== 5단계: Video Entity 업데이트 =====
    private void updateVideoEntity(Video video, String s3EncodedPath) {
        video.updateSavedPath(s3EncodedPath);
        video.updateVideoStatus(VideoStatus.ENCODED);
        videoRepository.save(video);
        log.info("Video Entity 업데이트 완료 - savedPath: {}", s3EncodedPath);
    }

    private void updateVideoEntityToFailed(Integer videoIdx) {
        try {
            Video video = videoRepository.findById(videoIdx).orElse(null);
            if (video != null) {
                video.updateVideoStatus(VideoStatus.FAILED);
                videoRepository.save(video);
            }
        } catch (Exception e) {
            log.error("Video Entity 실패 상태 업데이트 실패: {}", videoIdx, e);
        }
    }

    // ===== 6단계: 로컬 파일 정리 =====
    private void cleanupLocalFiles(String... paths) {
        for (String path : paths) {
            if (path != null) {
                try {
                    Path p = Paths.get(path);
                    if (Files.isDirectory(p)) {
                        Files.walk(p)
                                .sorted(Comparator.reverseOrder())
                                .map(Path::toFile)
                                .forEach(File::delete);
                    } else {
                        Files.deleteIfExists(p);
                    }
                    log.info("로컬 파일 정리 완료: {}", path);
                } catch (IOException e) {
                    log.error("로컬 파일 정리 실패: {}", path, e);
                }
            }
        }
    }
}