package com.dabom.score.model.dto;

import com.dabom.member.model.entity.Member;
import com.dabom.score.model.entity.Score;
import com.dabom.score.model.entity.ScoreType;
import com.dabom.video.model.Video;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScoreRegisterReqDto {

    private double score;
    private Member member;
    private Integer channelIdx;
    private Integer videoIdx;
    private ScoreType scoreType;

    public Score toEntity(Member user,Member channel,Video video){
        return Score.builder()
                .score(this.score)
                .member(user)
                .channel(channel)
                .video(video)
                .scoreType(this.scoreType)
                .build();
    }
}
