package com.dabom.playlist.model.dto;

import com.dabom.video.model.Video;
import lombok.Getter;

@Getter
public class VideoSimpleDto {
    private Integer idx;
    private String title;
    private String savedPath; // e.g., thumbnail path or m3u8 path

    public VideoSimpleDto(Video video) {
        this.idx = video.getIdx();
        this.title = video.getTitle();
        this.savedPath = video.getSavedPath();
    }
}
