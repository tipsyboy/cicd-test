package com.dabom.playlist.model.dto;

import com.dabom.member.model.entity.Member;
import com.dabom.playlist.model.entity.Playlist;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistUpdateDto {
    private Integer playlistIdx;
    private String playlistTitle;

    public void update(Playlist playlist) {
        playlist.setPlaylistTitle(this.playlistTitle);
    }
}