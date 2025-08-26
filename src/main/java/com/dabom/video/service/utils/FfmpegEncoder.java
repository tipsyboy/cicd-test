package com.dabom.video.service.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class FfmpegEncoder {

    private static final String VIDEO_UPLOAD_DIR = "videos/";
    private static final String INDEX_FILE_NAME = "index.m3u8";
    private static final String SEGMENT_PATTERN = "segment_%08d.ts";

    @Value("${file.ffmpeg.path}")
    private String ffmpegPath;

    @Value("${file.ffmpeg.hls.segment-duration}")
    private int hlsSegmentDuration;

    public String encode(String originalPath) throws IOException {
        log.info(">>>>>> start ffmpeg encoder >>>>>>");

        try {
            Path encodingDir = makeEncodingDir();
            Process process = buildProcessingCommand(originalPath, encodingDir);

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("Video 인코딩 실패 (exitCode={})", exitCode);
            }
            return encodingDir.toString();
        } catch (Exception e) {
            log.error("Video 인코딩 중 예외 발생", e);
        }
        return originalPath;
    }

    // ===== ===== //
    private Process buildProcessingCommand(String originalPath, Path encodingDir) throws IOException {
        Path segmentPattern = encodingDir.resolve(SEGMENT_PATTERN);
        Path indexFile = encodingDir.resolve(INDEX_FILE_NAME);

        ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath,
                "-i", originalPath,
                "-c", "copy",
                "-hls_time", String.valueOf(hlsSegmentDuration),
                "-hls_list_size", "0",
                "-f", "hls",
                "-hls_segment_filename", segmentPattern.toString(),
                "-y",
                indexFile.toString()
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        // FFmpeg 출력 로그 실시간 확인
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("FFmpeg: {}", line);
                }
            } catch (IOException e) {
                log.error("FFmpeg 로그 읽기 실패", e);
            }
        }).start();

        return process;
    }

    private Path makeEncodingDir() throws IOException {
        UUID uuid = UUID.randomUUID();

        Path encodingDir = Paths.get(
                VIDEO_UPLOAD_DIR,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy/MM/dd")),
                uuid.toString()
        );
        Files.createDirectories(encodingDir);
        return encodingDir;
    }
}
