package com.dabom.s3;

public record PresignedUrlRequestDto(String originalFilename, Long fileSize, String contentType) {
}
