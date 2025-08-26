package com.dabom.video.model.dto;

public record PresignedUrlRequestDto(String originalFilename, Long fileSize, String contentType) {
}
