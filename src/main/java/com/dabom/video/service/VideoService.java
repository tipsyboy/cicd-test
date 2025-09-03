package com.dabom.video.service;

import com.dabom.member.exception.MemberException;
import com.dabom.member.exception.MemberExceptionType;
import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.score.model.entity.Score;
import com.dabom.score.model.entity.ScoreType;
import com.dabom.score.repository.ScoreRepository;
import com.dabom.video.exception.VideoException;
import com.dabom.video.exception.VideoExceptionType;
import com.dabom.video.model.Video;
import com.dabom.video.model.dto.VideoInformationResponseDto;
import com.dabom.video.model.dto.VideoMetadataRequestDto;
import com.dabom.video.model.dto.score.VideoScoreRequestDto;
import com.dabom.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final ScoreRepository scoreRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Integer mappingMetadata(VideoMetadataRequestDto requestDto) {
        Video video = videoRepository.findById(requestDto.getIdx())
                .orElseThrow(() -> new VideoException(VideoExceptionType.VIDEO_NOT_FOUND));

        video.mappingVideoMetadata(
                requestDto.getTitle(),
                requestDto.getDescription(),
                requestDto.isPublic(),
                requestDto.getVideoTag()
        );

        return video.getIdx();
    }

    public List<VideoInformationResponseDto> getVideoListByChannelName(String channelName) {
        Member channel = memberRepository.findByNameWithVideos(channelName)
                .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        return channel.getVideoList().stream()
                .map(VideoInformationResponseDto::toDto)
                .toList();
    }

    public List<VideoInformationResponseDto> getVideoListByLoginMember(Integer memberIdx) {
        Member channel = memberRepository.findByIdWithVideos(memberIdx)
                .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        return channel.getVideoList().stream()
                .map(VideoInformationResponseDto::toDto)
                .toList();
    }

    @Transactional
    public void scoreVideo(VideoScoreRequestDto requestDto, Integer memberIdx, Integer videoIdx) {
        Member scoreSender = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Video targetVideo = videoRepository.findById(videoIdx)
                .orElseThrow(() -> new VideoException(VideoExceptionType.VIDEO_NOT_FOUND));

        if (scoreRepository.existsByMemberAndVideo(scoreSender, targetVideo)) {
            throw new VideoException(VideoExceptionType.VIDEO_ALREADY_RATED);
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

    public Integer deleteVideo(Integer videoIdx, Integer memberIdx) {
        Video video = videoRepository.findById(videoIdx)
                .orElseThrow(() -> new VideoException(VideoExceptionType.VIDEO_NOT_FOUND));
        Member channel = video.getChannel();

        if (!channel.getIdx().equals(memberIdx)) {
            throw new VideoException(VideoExceptionType.PERMISSION_DENIED);
        }

        video.deleteVideo();

        return videoIdx;
    }
}
