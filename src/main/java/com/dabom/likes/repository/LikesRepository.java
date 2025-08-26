package com.dabom.likes.repository;

import com.dabom.likes.model.likes.Likes;
import com.dabom.member.model.entity.Member;
import com.dabom.video.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes,Integer> {
    Optional<Likes> findByChannelAndVideo(Member member, Video video);
}
