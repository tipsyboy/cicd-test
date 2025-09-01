package com.dabom.score.repository;

import com.dabom.member.model.entity.Member;
import com.dabom.score.model.entity.Score;
import com.dabom.score.model.entity.ScoreType;
import com.dabom.video.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Integer> {
    boolean existsByMemberAndChannelAndIsDeletedFalse(Member member, Member channel);

    boolean existsByMemberAndVideoAndIsDeletedFalse(Member member, Video video);

    boolean existsByMemberAndVideo(Member member, Video video);

    Optional<Score> findByMemberIdxAndVideoIdxAndIsDeletedFalse(Integer memberIdx, Integer videoIdx);

    @Query("SELECT AVG(s.score) FROM Score s WHERE s.scoreType = :scoreType AND s.isDeleted = FALSE AND " +
            "( (:scoreType = 'CHANNEL' AND s.channel.idx = :targetIdx) OR " +
            "  (:scoreType = 'VIDEO' AND s.video.idx = :targetIdx) )")
    Optional<Double> findAverageScoreByScoreTypeAndTargetIdx(@Param("scoreType") ScoreType scoreType, @Param("targetIdx") Integer targetIdx);

}
