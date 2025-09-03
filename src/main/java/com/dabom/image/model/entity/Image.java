package com.dabom.image.model.entity;

import com.dabom.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private String originalName;
    private String imageName;
    private String imageUrl;
    private String imagePath;
    private Long fileSize;
    private Boolean isDeleted;

    @Builder
    public Image(Integer idx, String originalName, String imageName, String imageUrl, String imagePath, Long fileSize) {
        this.idx = idx;
        this.originalName = originalName;
        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.imagePath = imagePath;
        this.fileSize = fileSize;
        this.isDeleted = false;
    }
    public void safeDelete() {
        this.isDeleted = true;
    }
}
