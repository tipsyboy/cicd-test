package com.dabom.likes.repository;

import com.dabom.boardcomment.model.entity.BoardComment;
import com.dabom.channelboard.model.entity.ChannelBoard;
import com.dabom.likes.model.likes.Likes;
import com.dabom.member.model.entity.Member;
import com.dabom.video.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes,Integer> {
    Optional<Likes> findByChannelAndBoardComment(Member member, BoardComment boardComment);

    Optional<Likes> findByChannelAndChannelBoard(Member member, ChannelBoard channelBoard);
}
