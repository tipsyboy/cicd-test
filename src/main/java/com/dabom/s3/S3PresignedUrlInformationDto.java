package com.dabom.s3;

import lombok.Builder;

@Builder
public record S3PresignedUrlInformationDto(String uploadUrl, Integer expiresIn) {
}
