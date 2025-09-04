package com.dabom.image.model.dto;

import com.dabom.image.model.entity.ImageType;

public record ImageCreateRequestDto(String originalFilename, Long fileSize, String contentType,
                                    String s3Key, ImageType imageType) {
}
