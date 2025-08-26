package com.dabom.video.service.s3;

import com.dabom.video.model.Video;
import com.dabom.video.model.VideoStatus;
import com.dabom.video.model.dto.PresignedUrlRequestDto;
import com.dabom.video.model.dto.PresignedUrlResponseDto;
import com.dabom.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoS3UploadService {

    private static final Long MAX_FILE_SIZE = 1024 * 1024 * 100L;
    private static final String VIDEO_TEMP_PATH = "videos/original/";

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.s3.presigned-url-duration}")
    private int presignedUrlDuration;

    private final S3Presigner s3Presigner;
    private final VideoRepository videoRepository;

    public PresignedUrlResponseDto generatePresignedUrl(PresignedUrlRequestDto requestDto) {
        // 1. 파일 검증
//        validateFile(request);

        // 2. S3 Key 생성 (UUID 기반)
        String s3Key = generateS3Key(requestDto.originalFilename());

        // 3. Video Entity 생성
        Integer videoIdx = createVideoEntity(requestDto, s3Key);

        // 4. Presigned URL 생성
        String uploadUrl = createPresignedUrl(s3Key, requestDto.contentType());

        return PresignedUrlResponseDto.builder()
                .videoIdx(videoIdx)
                .uploadUrl(uploadUrl)
                .s3Key(s3Key)
                .expiresIn(presignedUrlDuration)
                .build();
    }

    private String generateS3Key(String originalFileName) {
        String todayPath = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String extension = extractExtension(originalFileName);
        String uuid = UUID.randomUUID().toString();

        return VIDEO_TEMP_PATH + todayPath + "/" + uuid + "." + extension;
    }

    private Integer createVideoEntity(PresignedUrlRequestDto requestDto, String s3Key) {
        Video video = Video.builder()
                .originalFilename(requestDto.originalFilename())
                .originalPath(s3Key)
                .originalSize(requestDto.fileSize())
                .status(VideoStatus.UPLOADING)
                .contentType(requestDto.contentType())
                .build();
        return videoRepository.save(video).getIdx();
    }

    private String createPresignedUrl(String s3Key, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(presignedUrlDuration))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        return presignedRequest.url().toString();
    }

    private String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
            throw new IllegalArgumentException("파일 확장자가 없습니다.");
        }
        return fileName.substring(dotIndex + 1).toLowerCase();
    }


    private void validateFile(PresignedUrlRequestDto requestDto) {
        // 파일 크기 검증 (100MB 제한)
        if (requestDto.fileSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기는 100MB를 초과할 수 없습니다.");
        }

        // 컨텐츠 타입 검증
        if (!requestDto.contentType().startsWith("video/")) {
            throw new IllegalArgumentException("비디오 파일만 업로드 가능합니다.");
        }

        // 지원 확장자 검증
        String extension = extractExtension(requestDto.originalFilename());
        if (!isValidVideoExtension(extension)) {
            throw new IllegalArgumentException("지원하지 않는 비디오 형식입니다.");
        }
    }

    private boolean isValidVideoExtension(String extension) {
//        mp4|avi|mkv|mov|wmv|flv|webm|m4v
        return extension.matches("mp4");
    }
}