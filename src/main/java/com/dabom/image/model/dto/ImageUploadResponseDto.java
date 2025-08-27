package com.dabom.image.model.dto;

import com.dabom.image.model.entity.Image;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "이미지 업로드 응답 DTO")
public class ImageUploadResponseDto {

    @Schema(description = "파일idx", example = "123")
    private Integer idx;

    @Schema(description = "원본 파일 이름", example = "example.jpg")
    private String originalName;

    @Schema(description = "저장된 파일 이름 (UUID 포함)", example = "uuid1234.jpg")
    private String imageName;

    @Schema(description = "파일의 서버 저장 경로", example = "/Uploads/uuid1234.jpg")
    private String imagePath;

    @Schema(description = "파일의 접근 URL", example = "http://localhost:8080/Uploads/uuid1234.jpg")
    private String imageUrl;

    @Schema(description = "파일 크기 (바이트 단위)", example = "102400")
    private Long imageSize;

    public static ImageUploadResponseDto from(Image entity) {
        return ImageUploadResponseDto.builder()
                .originalName(entity.getOriginalName())
                .imageName(entity.getImageName())
                .imagePath(entity.getImagePath())
                .imageUrl(entity.getImageUrl())
                .imageSize(entity.getFileSize())
                .build();
    }
}