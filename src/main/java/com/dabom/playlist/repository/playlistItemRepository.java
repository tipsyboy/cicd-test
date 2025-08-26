package com.dabom.playlist.repository;

import com.dabom.playlist.model.entity.Playlist;
import com.dabom.playlist.model.entity.PlaylistItem;
import com.dabom.video.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface playlistItemRepository extends JpaRepository<PlaylistItem,Integer> {

   boolean existsByPlaylistAndVideo(Playlist playlist, Video video);

   Optional<PlaylistItem> findByPlaylistAndVideo(Playlist playlist, Video video);

    List<PlaylistItem> findAllByPlaylist(Playlist playlist);

}

