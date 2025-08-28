package com.dabom.video.controller;

import com.dabom.common.BaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.video.model.dto.PresignedUrlRequestDto;
import com.dabom.video.model.dto.PresignedUrlResponseDto;
import com.dabom.video.service.local.VideoLocalUploadService;
import com.dabom.video.service.s3.VideoS3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Slf4j
@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoUploadController {

    private final VideoLocalUploadService videoUploadService;
    private final VideoS3UploadService videoS3UploadService;

    @PostMapping("/upload")
    public ResponseEntity<Integer> upload(@RequestPart MultipartFile file,
                                          @AuthenticationPrincipal MemberDetailsDto loginMember) throws IOException {
        return ResponseEntity.ok(videoUploadService.upload(file, loginMember.getIdx()));
    }

    @PostMapping("/presigned")
    public ResponseEntity<BaseResponse<PresignedUrlResponseDto>> getPresignedUrl(@RequestBody PresignedUrlRequestDto requestDto,
                                                                                 @AuthenticationPrincipal MemberDetailsDto loginMember) {
        PresignedUrlResponseDto response = videoS3UploadService.generatePresignedUrl(requestDto, loginMember.getIdx());
        return ResponseEntity.ok(BaseResponse.of(response, HttpStatus.OK));
    }

}