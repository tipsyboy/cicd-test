package com.dabom.score.model.entity;

import com.dabom.common.BaseEntity;
import com.dabom.member.model.entity.Member;
import com.dabom.score.exception.ScoreException;
import com.dabom.score.exception.ScoreExceptionType;
import com.dabom.video.model.Video;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "scores")  // 테이블 이름 명시
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Score extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(nullable = false)
    private double score;

    @NotNull(message = "평점자는 필수입니다.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_idx")
    private Member channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_idx")
    private Video video;

    @NotNull(message = "ScoreType은 필수입니다.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScoreType scoreType;

    private Boolean isDeleted;

    @Builder
    public Score(double score, Member member, Member channel, Video video, ScoreType scoreType) {
        this.score = score;
        this.member = member;
        this.channel = channel;
        this.video = video;
        this.scoreType = scoreType;
        this.isDeleted = false;
    }

    @PrePersist
    @PreUpdate
    private void validate() {
        if (score < 0.0 || score > 5.0) {
            throw new ScoreException(ScoreExceptionType.INVALID_SCORE_RANGE);
        }
        if (member == null) {
            throw new ScoreException(ScoreExceptionType.MEMBER_NOT_FOUND);
        }
        if ((channel == null && video == null) || (channel != null && video != null)) {
            throw new ScoreException(ScoreExceptionType.TARGET_NOT_SPECIFIED);
        }
        if (channel != null && scoreType != ScoreType.CHANNEL) {
            throw new ScoreException(ScoreExceptionType.SCORE_TYPE_MISMATCH);
        }
        if (video != null && scoreType != ScoreType.VIDEO) {
            throw new ScoreException(ScoreExceptionType.SCORE_TYPE_MISMATCH);
        }
    }

    public void softDelete(){
        this.isDeleted = true;
    }
}