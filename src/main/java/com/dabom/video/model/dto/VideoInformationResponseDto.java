package com.dabom.video.model.dto;

import com.dabom.video.model.Video;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoInformationResponseDto {

    private Integer videoIdx;
    private String title;
    private String description;

//    private Long views;

    private boolean isPublicVideo;

    private Long totalReviewerCount;
    private double averageScore;

    public static VideoInformationResponseDto toDto(Video entity) {
        return VideoInformationResponseDto.builder()
                .videoIdx(entity.getIdx())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .isPublicVideo(entity.isPublic())
                .totalReviewerCount(entity.getTotalReviewerCount())
                .averageScore(entity.getAverageScore())
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private VideoInformationResponseDto(Integer videoIdx, String title, String description, boolean isPublicVideo, Long totalReviewerCount, double averageScore) {
        this.videoIdx = videoIdx;
        this.title = title;
        this.description = description;
        this.isPublicVideo = isPublicVideo;
        this.totalReviewerCount = totalReviewerCount;
        this.averageScore = averageScore;
    }
}
