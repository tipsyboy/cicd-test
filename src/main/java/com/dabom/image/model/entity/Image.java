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
    private String originalName;
    private String imageName;
    private String imageUrl;
    private String imagePath;
    private Long fileSize;
    private Boolean isDeleted;

    @OneToOne(mappedBy = "profileImage")
    private Member profileOwner;

    @OneToOne(mappedBy = "bannerImage")
    private Member bannerOwner;

    @Builder
    public Image(Integer idx, String originalName, String imageName, String imageUrl, String imagePath, Long fileSize,
                 Member profileOwner, Member bannerOwner) {
        this.idx = idx;
        this.originalName = originalName;
        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.imagePath = imagePath;
        this.fileSize = fileSize;
        this.isDeleted = false;
        this.profileOwner = profileOwner;
        this.bannerOwner = bannerOwner;
    }
    public void safeDelete() {
        this.isDeleted = true;
    }
}
