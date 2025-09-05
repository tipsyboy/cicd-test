package com.dabom.image.controller;

import com.dabom.common.BaseResponse;
import com.dabom.image.constants.ImageSwaggerConstants;
import com.dabom.image.model.dto.ImageCreateRequestDto;
import com.dabom.image.model.dto.PresignedUrlResponseDto;
import com.dabom.image.service.S3ImageServiceV2;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.s3.PresignedUrlRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "이미지 관리 기능 V2", description = "S3 Presigned URL을 이용한 이미지 관리 API")
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageControllerV2 {

    private final S3ImageServiceV2 s3ImageServiceV2;

    @Operation(summary = "프로필 이미지 Presigned URL 생성", description = "프로필 이미지 업로드를 위한 Presigned URL을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Presigned URL 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ImageSwaggerConstants.IMAGE_UPLOAD_PRESIGNED_URL_RESPONSE)))
    })
    @PostMapping("/presigned/profile")
    public ResponseEntity<BaseResponse<PresignedUrlResponseDto>> getProfileImagePresignedUrl(@RequestBody PresignedUrlRequestDto requestDto) {
        PresignedUrlResponseDto presigned = s3ImageServiceV2.getProfileImagePresignedUrl(requestDto);
        return ResponseEntity.ok(BaseResponse.of(presigned, HttpStatus.OK));
    }

    @Operation(summary = "배너 이미지 Presigned URL 생성", description = "배너 이미지 업로드를 위한 Presigned URL을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Presigned URL 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ImageSwaggerConstants.IMAGE_UPLOAD_PRESIGNED_URL_RESPONSE)))
    })
    @PostMapping("/presigned/banner")
    public ResponseEntity<BaseResponse<PresignedUrlResponseDto>> getBannerImagePresignedUrl(@RequestBody PresignedUrlRequestDto requestDto) {
        PresignedUrlResponseDto presigned = s3ImageServiceV2.getBannerImagePresignedUrl(requestDto);
        return ResponseEntity.ok(BaseResponse.of(presigned, HttpStatus.OK));
    }

    @Operation(summary = "썸네일 이미지 Presigned URL 생성", description = "썸네일 이미지 업로드를 위한 Presigned URL을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Presigned URL 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ImageSwaggerConstants.IMAGE_UPLOAD_PRESIGNED_URL_RESPONSE)))
    })
    @PostMapping("/presigned/thumbnail")
    public ResponseEntity<BaseResponse<PresignedUrlResponseDto>> getThumbnailImagePresignedUrl(@RequestBody PresignedUrlRequestDto requestDto) {
        PresignedUrlResponseDto presigned = s3ImageServiceV2.getThumbnailPresignedUrl(requestDto);
        return ResponseEntity.ok(BaseResponse.of(presigned, HttpStatus.OK));
    }


    @Operation(summary = "이미지 정보 등록", description = "S3에 업로드된 이미지의 정보를 시스템에 등록합니다.")
    @PostMapping
    public ResponseEntity<BaseResponse<Integer>> registerImage(@RequestBody ImageCreateRequestDto requestDto,
                                                               @AuthenticationPrincipal MemberDetailsDto loginMember) {
        Integer imageIdx = s3ImageServiceV2.createMemberImage(requestDto, loginMember.getIdx());
        return ResponseEntity.ok(BaseResponse.of(imageIdx, HttpStatus.OK, "이미지가 등록되었습니다."));
    }

    @Operation(summary = "썸네일 이미지 정보 등록", description = "S3에 업로드된 썸네일 이미지의 정보를 시스템에 등록하고 비디오와 연결합니다.")
    @PostMapping("/thumbnail/{videoIdx}")
    public ResponseEntity<BaseResponse<Integer>> registerImage(@RequestBody ImageCreateRequestDto requestDto,
                                                               @PathVariable Integer videoIdx) {
        Integer imageIdx = s3ImageServiceV2.createThumbnailImage(requestDto, videoIdx);
        return ResponseEntity.ok(BaseResponse.of(imageIdx, HttpStatus.OK, "이미지가 등록되었습니다."));
    }


}