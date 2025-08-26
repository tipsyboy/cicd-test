package com.dabom.score.model.dto;

import com.dabom.member.model.entity.Member;
import com.dabom.score.model.entity.Score;
import com.dabom.score.model.entity.ScoreType;
import com.dabom.video.model.Video;
import lombok.Getter;

@Getter
public class ScoreUpdateReqDto {
    private Integer idx;
    private double score;
    private Member member;
    private Member channel;
    private Video video;
    private ScoreType scoreType;

    public Score toEntity(){
        return Score.builder()
                .score(this.score)
                .member(this.member)
                .channel(this.channel)
                .video(this.video)
                .scoreType(this.scoreType)
                .build();
    }
}
