package com.dabom.image.controller;

import com.dabom.common.BaseResponse;
import com.dabom.image.model.dto.ImageCreateRequestDto;
import com.dabom.image.model.dto.PresignedUrlResponseDto;
import com.dabom.image.service.S3ImageServiceV2;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.s3.PresignedUrlRequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "이미지 관리 기능")
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageControllerV2 {

    private final S3ImageServiceV2 s3ImageServiceV2;

    @PostMapping("/presigned/profile")
    public ResponseEntity<BaseResponse<PresignedUrlResponseDto>> getProfileImagePresignedUrl(@RequestBody PresignedUrlRequestDto requestDto) {
        PresignedUrlResponseDto presigned = s3ImageServiceV2.getProfileImagePresignedUrl(requestDto);
        return ResponseEntity.ok(BaseResponse.of(presigned, HttpStatus.OK));
    }

    @PostMapping("/presigned/banner")
    public ResponseEntity<BaseResponse<PresignedUrlResponseDto>> getBannerImagePresignedUrl(@RequestBody PresignedUrlRequestDto requestDto) {
        PresignedUrlResponseDto presigned = s3ImageServiceV2.getBannerImagePresignedUrl(requestDto);
        return ResponseEntity.ok(BaseResponse.of(presigned, HttpStatus.OK));
    }

    @PostMapping("/presigned/thumbnail")
    public ResponseEntity<BaseResponse<PresignedUrlResponseDto>> getThumbnailImagePresignedUrl(@RequestBody PresignedUrlRequestDto requestDto) {
        PresignedUrlResponseDto presigned = s3ImageServiceV2.getThumbnailPresignedUrl(requestDto);
        return ResponseEntity.ok(BaseResponse.of(presigned, HttpStatus.OK));
    }


    @PostMapping
    public ResponseEntity<BaseResponse<Integer>> registerImage(@RequestBody ImageCreateRequestDto requestDto,
                                                               @AuthenticationPrincipal MemberDetailsDto loginMember) {
        Integer imageIdx = s3ImageServiceV2.createMemberImage(requestDto, loginMember.getIdx());
        return ResponseEntity.ok(BaseResponse.of(imageIdx, HttpStatus.OK, "이미지가 등록되었습니다."));
    }

    @PostMapping("/thumbnail/{videoIdx}")
    public ResponseEntity<BaseResponse<Integer>> registerImage(@RequestBody ImageCreateRequestDto requestDto,
                                                               @PathVariable Integer videoIdx) {
        Integer imageIdx = s3ImageServiceV2.createThumbnailImage(requestDto, videoIdx);
        return ResponseEntity.ok(BaseResponse.of(imageIdx, HttpStatus.OK, "이미지가 등록되었습니다."));
    }


}