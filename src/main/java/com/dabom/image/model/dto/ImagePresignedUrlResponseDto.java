package com.dabom.image.model.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImagePresignedUrlResponseDto {

    private String uploadUrl;
    private String s3Key;
    private Integer expiresIn;

    @Builder
    private ImagePresignedUrlResponseDto(String uploadUrl, String s3Key, Integer expiresIn) {
        this.uploadUrl = uploadUrl;
        this.s3Key = s3Key;
        this.expiresIn = expiresIn;
    }
}
