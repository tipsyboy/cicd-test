package com.dabom.playlist.model.entity;

import com.dabom.video.model.Video;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaylistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_idx")
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    private Video video;

    public PlaylistItem(Playlist playlist, Video video){
        this.playlist = playlist;
        this.video = video;
    }

}
