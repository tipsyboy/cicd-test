package com.dabom.video.controller;

import com.dabom.common.BaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.s3.PresignedUrlRequestDto;
import com.dabom.video.constansts.SwaggerConstants;
import com.dabom.video.model.dto.VideoPresignedUrlResponseDto;
import com.dabom.video.service.local.VideoLocalUploadService;
import com.dabom.video.service.s3.VideoS3UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Tag(name = "비디오 업로드 기능", description = "비디오 업로드 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoUploadController {

    private final VideoLocalUploadService videoUploadService;
    private final VideoS3UploadService videoS3UploadService;

    @Operation(summary = "로컬 비디오 업로드", description = "서버에 직접 비디오 파일을 업로드합니다. (테스트용)")
    @PostMapping("/upload")
    public ResponseEntity<Integer> upload(@RequestPart MultipartFile file,
                                          @AuthenticationPrincipal MemberDetailsDto loginMember) throws IOException {
        return ResponseEntity.ok(videoUploadService.upload(file, loginMember.getIdx()));
    }

    @Operation(summary = "S3 Presigned URL 생성", description = "S3에 비디오를 업로드하기 위한 Presigned URL을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Presigned URL 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = SwaggerConstants.VIDEO_UPLOAD_RESPONSE)))
    })
    @PostMapping("/presigned")
    public ResponseEntity<BaseResponse<VideoPresignedUrlResponseDto>> getPresignedUrl(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Presigned URL 요청 정보",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PresignedUrlRequestDto.class),
                            examples = @ExampleObject(value = SwaggerConstants.VIDEO_UPLOAD_REQUEST)
                    )
            )
            @RequestBody PresignedUrlRequestDto requestDto,
            @AuthenticationPrincipal MemberDetailsDto loginMember) {
        VideoPresignedUrlResponseDto response = videoS3UploadService.generatePresignedUrl(requestDto, loginMember.getIdx());
        return ResponseEntity.ok(BaseResponse.of(response, HttpStatus.OK));
    }

}