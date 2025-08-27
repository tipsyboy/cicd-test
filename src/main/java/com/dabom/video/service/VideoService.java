package com.dabom.video.service;

import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.score.model.entity.Score;
import com.dabom.score.model.entity.ScoreType;
import com.dabom.score.repository.ScoreRepository;
import com.dabom.video.model.Video;
import com.dabom.video.model.dto.VideoMetadataRequestDto;
import com.dabom.video.model.dto.score.VideoScoreRequestDto;
import com.dabom.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final ScoreRepository scoreRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Integer mappingMetadata(VideoMetadataRequestDto requestDto) {
        Video video = videoRepository.findById(requestDto.getIdx())
                .orElseThrow(() -> new IllegalArgumentException("비디오를 찾을 수 없습니다. idx=" + requestDto.getIdx()));

        video.mappingVideoMetadata(
                requestDto.getTitle(),
                requestDto.getDescription(),
                requestDto.isPublic(),
                requestDto.getVideoTag()
        );

        return video.getIdx();
    }

    @Transactional
    public void scoreVideo(VideoScoreRequestDto requestDto, Integer memberIdx, Integer videoIdx) {
        Member scoreSender = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다. idx=" + memberIdx));

        Video targetVideo = videoRepository.findById(videoIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 비디오가 존재하지 않습니다. idx=" + videoIdx));

        if (scoreRepository.existsByMemberAndVideo(scoreSender, targetVideo)) {
            throw new IllegalArgumentException("이미 평가 완료된 영상입니다");
        }

        targetVideo.addScore(requestDto.score());
        Score score = Score.builder()
                .member(scoreSender)
                .video(targetVideo)
                .score(requestDto.score())
                .scoreType(ScoreType.VIDEO)
                .build();
        scoreRepository.save(score);
    }
}
