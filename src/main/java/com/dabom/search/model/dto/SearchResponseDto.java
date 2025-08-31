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
    //    private String duration;
//    private String views;
//    private Double rating;
    private Boolean isPublic;
    private Integer uploadedAt;
    private ChannelInfo channel;

    //필요한 것
    // 비디오entity에
    //      duration
    //      viewCount
    //      rating
    //      필드 추가하고
    // Member 연관관계 맺기

    @Getter
    @Builder
    public static class ChannelInfo {
        private Integer idx;
        private String email;
        private String name;
        private String content;
    }



    public static SearchResponseDto from(Video video) {
        return SearchResponseDto.builder()
                .videoId(video.getIdx())
                .title(video.getTitle())
                .description(video.getDescription())
//                .duration(formatDuration(video.getDuration()))
//                .views(formatViews(video.getViewCount()))
//                .rating(video.getRating())
                .isPublic(video.isPublic())
                .uploadedAt(calculateDaysAgo(video.getCreatedAt()))
                .channel(ChannelInfo.builder()
                        .idx(video.getChannel().getIdx())
                        .content(video.getChannel().getName())
                        .build())
                .build();
    }

    private static String formatDuration(Long durationSeconds) {
        if (durationSeconds == null) return "00:00";

        long minutes = durationSeconds / 60;
        long seconds = durationSeconds % 60;

        if (minutes >= 60) {
            long hours = minutes / 60;
            minutes = minutes % 60;
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%d:%02d", minutes, seconds);
    }

    private static String formatViews(Long viewCount) {
        if (viewCount == null || viewCount == 0) return "0";

        if (viewCount >= 1000000) {
            return String.format("%.1fM", viewCount / 1000000.0);
        } else if (viewCount >= 1000) {
            return String.format("%.1fK", viewCount / 1000.0);
        }
        return viewCount.toString();
    }

    private static Integer calculateDaysAgo(LocalDateTime createdAt) {
        if (createdAt == null) return 0;
        return (int) ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
    }
}