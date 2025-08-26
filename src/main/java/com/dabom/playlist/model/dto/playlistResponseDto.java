package com.dabom.playlist.model.dto;

import com.dabom.playlist.model.entity.Playlist;
import com.dabom.playlist.model.entity.PlaylistItem;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Builder
public class playlistResponseDto {
    private Integer idx;
    private String playlistTitle;
    private String createdAt;
    private String updatedAt;
    private Boolean isModified;
    private List<PlaylistItem> items;

    public static  playlistResponseDto from(Playlist entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return playlistResponseDto.builder()
                .idx(entity.getIdx())
                .playlistTitle(entity.getPlaylistTitle())
                .createdAt(entity.getCreatedAt().format(formatter))
                .updatedAt(entity.getUpdatedAt().format(formatter))
                .isModified(!entity.getCreatedAt().equals(entity.getUpdatedAt()))
                .items(entity.getItems())
                .build();
    }
}
