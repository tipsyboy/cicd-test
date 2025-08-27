package com.dabom.playlist.repository;

import com.dabom.member.model.entity.Member;
import com.dabom.playlist.model.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist,Integer> {
    List<Playlist> findAllByMember(Member member);
}
