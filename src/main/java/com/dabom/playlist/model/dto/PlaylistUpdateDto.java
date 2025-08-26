package com.dabom.playlist.model.dto;

import com.dabom.member.model.entity.Member;
import com.dabom.playlist.model.entity.Playlist;
import lombok.Getter;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Getter
@Setter
public class PlaylistUpdateDto {
    private String playlistTitle;
    private List<Integer> videoIdsToAdd;
    private List<Integer> videoIdsToRemove;

//    public PlaylistUpdateDto(Member member){
//        return Playlist.builder()
//                .playlistTitle(this.playlistTitle)
//                .
//                .build()
//    }

}