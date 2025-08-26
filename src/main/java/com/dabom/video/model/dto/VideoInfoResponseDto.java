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

    @Builder(access = AccessLevel.PRIVATE)
    private VideoInfoResponseDto(Integer idx, String title, String description, String savedPath) {
        this.idx = idx;
        this.title = title;
        this.description = description;
        this.savedPath = savedPath;
    }

    public static VideoInfoResponseDto toDto(Video entity, String savedPath) {
        return VideoInfoResponseDto.builder()
                .idx(entity.getIdx())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .savedPath(savedPath)
                .build();
    }
}
