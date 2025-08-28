package com.dabom.playlist.model.entity;

import com.dabom.common.BaseEntity;
import com.dabom.member.model.entity.Member;
import com.dabom.video.model.Video;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Playlist extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;
    @Setter
    private String playlistTitle;

    @OneToMany(mappedBy = "playlist", fetch = FetchType.LAZY)
    private List<PlaylistItem> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    private Member member;

    @Builder
    public Playlist(String playlistTitle, Member member) {
        this.playlistTitle = playlistTitle;
        this.member = member;

    }

    public void updatetitle(String title){
        this.playlistTitle = title;
    }

}
