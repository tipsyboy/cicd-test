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

    @Query("SELECT AVG(s.score) FROM Score s WHERE s.scoreType = 'CHANNEL' AND s.isDeleted = FALSE AND s.channel.idx = :channelIdx")
    Optional<Double> findAverageScoreByChannelIdx(@Param("channelIdx") Integer channelIdx);

    // 비디오의 평균 점수를 계산하는 쿼리
    @Query("SELECT AVG(s.score) FROM Score s WHERE s.scoreType = 'VIDEO' AND s.isDeleted = FALSE AND s.video.idx = :videoIdx")
    Optional<Double> findAverageScoreByVideoIdx(@Param("videoIdx") Integer videoIdx);

}
