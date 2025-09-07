package com.dabom.search.model.dto;

import com.dabom.video.model.Video;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Builder
public class SearchResponseDto {
    private Integer videoId;
    private String title;
    private String description;
    private String views;
    private Double rating;
    private Boolean isPublic;
    private Integer uploadedAt;
    private ChannelInfo channel;
    private String videoImage;

    public static SearchResponseDto from(Video video, String profileImg, String thumbnail) {
        return SearchResponseDto.builder()
                .videoId(video.getIdx())
                .title(video.getTitle())
                .description(video.getDescription())
                .isPublic(video.isPublic())
                .videoImage(thumbnail)
                .uploadedAt(calculateDaysAgo(video.getCreatedAt()))
                .channel(ChannelInfo.builder()
                        .idx(video.getChannel().getIdx())
                        .name( video.getChannel().getName())
                        .content(video.getChannel().getContent())
                        .profileImg(profileImg)
                        .build())
                .build();
    }

    private static Integer calculateDaysAgo(LocalDateTime createdAt) {
        if (createdAt == null) return 0;
        return (int) ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
    }

    @Getter
    @Builder
    public static class ChannelInfo {
        private Integer idx;
        private String name;
        private String content;
        private String profileImg;
    }
}