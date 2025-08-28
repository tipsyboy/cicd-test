package com.dabom.video.service.local;


import com.dabom.member.exception.MemberException;
import com.dabom.member.exception.MemberExceptionType;
import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.video.model.Video;
import com.dabom.video.model.VideoStatus;
import com.dabom.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoLocalUploadService {

    private static final String VIDEO_TEMP_DIR = "temp/";

    private final VideoRepository videoRepository;
    private final MemberRepository memberRepository;

    public Integer upload(MultipartFile file, Integer memberIdx) throws IOException {
        Member channel = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("파일 이름이 없습니다.");
        }

        // 1. 파일 임시 저장.
        String uuid = UUID.randomUUID().toString();
        String savedTempPath = saveTempFile(file, originalFilename, uuid);

        Video video = Video.builder()
                .channel(channel)
                .originalFilename(originalFilename)
                .originalPath(savedTempPath)
                .originalSize(file.getSize())
                .status(VideoStatus.UPLOADING)
                .contentType(file.getContentType())
                .build();

        Video savedVideo = videoRepository.save(video);
        savedVideo.updateVideoStatus(VideoStatus.UPLOADED);
        return savedVideo.getIdx();
    }

    // ===== ===== //
    private String saveTempFile(MultipartFile file, String originalFilename, String uuid) throws IOException {
        String todayPath = createTodayUploadPath();
        String ext = extractExtension(originalFilename);

        String savedFilename = uuid + "." + ext;
        String savedPath = Paths.get(todayPath, savedFilename).toAbsolutePath().toString();
        file.transferTo(new File(savedPath));

        return savedPath;
    }

    private String createTodayUploadPath() throws IOException {
        String todayDir = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path uploadDir = Paths.get(VIDEO_TEMP_DIR, todayDir);
        Files.createDirectories(uploadDir);

        return uploadDir.toString();
    }

    private String extractExtension(String originalFileName) {
        int pos = originalFileName.lastIndexOf(".");
        return originalFileName.substring(pos + 1);
    }
}
