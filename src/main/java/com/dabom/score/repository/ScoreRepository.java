package com.dabom.score.repository;

import com.dabom.member.model.entity.Member;
import com.dabom.score.model.entity.Score;
import com.dabom.video.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<Score,Integer> {
    boolean existsByMemberAndChannelAndIsDeletedFalse(Member member, Member channel);
    boolean existsByMemberAndVideoAndIsDeletedFalse(Member member, Video video);
}
