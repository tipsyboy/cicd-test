package com.dabom.video.model.dto;

import com.dabom.video.model.Video;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoInfoResponseDto {

    private Integer idx;

    private String title;
    private String description;
    private String savedPath;
    private String channelName;
    private Long viewCount;
    private Long subscribeCount;

    @Builder(access = AccessLevel.PRIVATE)
    private VideoInfoResponseDto(Integer idx, String title, String description, String savedPath, String channelName, Long viewCount, Long subscribeCount) {
        this.idx = idx;
        this.title = title;
        this.description = description;
        this.savedPath = savedPath;
        this.channelName = channelName;
        this.viewCount = viewCount;
        this.subscribeCount = subscribeCount;
    }

    public static VideoInfoResponseDto toDto(Video entity, String savedPath) {
        return VideoInfoResponseDto.builder()
                .idx(entity.getIdx())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .viewCount(entity.getViews())
                .subscribeCount(entity.getChannel().getSubscribeCount())
                .channelName(entity.getChannel().getName())
                .savedPath(savedPath)
                .build();
    }
}
