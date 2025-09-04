package com.dabom.image.model.entity;

import com.dabom.common.BaseEntity;
import com.dabom.member.model.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    private String originalFilename;
    private String savedPath;
    
    private String contentType; // 이미지 타입
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    private Boolean isDeleted;

    @Builder
    public Image(String originalFilename, String savedPath, String contentType, Long fileSize, ImageType imageType) {
        this.originalFilename = originalFilename;
        this.savedPath = savedPath;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.imageType = imageType;
        this.isDeleted = false;
    }

    public void safeDelete() {
        this.isDeleted = true;
    }
}
