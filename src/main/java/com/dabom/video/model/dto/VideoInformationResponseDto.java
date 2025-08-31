package com.dabom.video.model.dto;

import com.dabom.video.model.Video;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoInformationResponseDto {

    private Integer videoIdx;
    private String title;
    private String description;
    private Long views;
    private Long totalReviewerCount;
    private double averageScore;
    private boolean isPublicVideo;
    private LocalDateTime uploadedAt;

    public static VideoInformationResponseDto toDto(Video entity) {
        return VideoInformationResponseDto.builder()
                .videoIdx(entity.getIdx())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .views(0L) // TODO: views 변경
                .averageScore(entity.getAverageScore())
                .isPublicVideo(entity.isPublic())
                .uploadedAt(entity.getCreatedAt())
                .build();
    }


    @Builder(access = AccessLevel.PRIVATE)
    private VideoInformationResponseDto(Integer videoIdx, String title, String description, Long views, Long totalReviewerCount, double averageScore, boolean isPublicVideo, LocalDateTime uploadedAt) {
        this.videoIdx = videoIdx;
        this.title = title;
        this.description = description;
        this.views = views;
        this.totalReviewerCount = totalReviewerCount;
        this.averageScore = averageScore;
        this.isPublicVideo = isPublicVideo;
        this.uploadedAt = uploadedAt;
    }
}
