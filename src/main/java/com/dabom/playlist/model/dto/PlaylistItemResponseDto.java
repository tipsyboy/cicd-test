package com.dabom.playlist.model.dto;

import com.dabom.playlist.model.entity.PlaylistItem;
import lombok.Getter;

@Getter
public class PlaylistItemResponseDto {
    private Integer idx;
    private VideoSimpleDto video;

    public PlaylistItemResponseDto(PlaylistItem playlistItem) {
        this.idx = playlistItem.getIdx();
        this.video = new VideoSimpleDto(playlistItem.getVideo());
    }
}
