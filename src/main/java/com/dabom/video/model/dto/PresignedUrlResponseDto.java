package com.dabom.video.model.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PresignedUrlResponseDto {

    private Integer videoIdx;
    private String uploadUrl;
    private String s3Key;
    private Integer expiresIn;

    @Builder
    private PresignedUrlResponseDto(Integer videoIdx, String uploadUrl, String s3Key, Integer expiresIn) {
        this.videoIdx = videoIdx;
        this.uploadUrl = uploadUrl;
        this.s3Key = s3Key;
        this.expiresIn = expiresIn;
    }

}
