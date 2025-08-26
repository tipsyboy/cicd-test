package com.dabom.playlist.model.dto;

import com.dabom.member.model.entity.Member;
import com.dabom.playlist.model.entity.Playlist;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlaylistRegisterDto {
    private String playlistTitle;
    private List<Integer> videoIds;

    public Playlist toEntity(Member member){
        return Playlist.builder()
                .playlistTitle(this.playlistTitle)
                .member(member)
                .build();
    }
}
