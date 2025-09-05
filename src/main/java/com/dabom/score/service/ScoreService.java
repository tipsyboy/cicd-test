package com.dabom.score.service;

import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.score.exception.ScoreException;
import com.dabom.score.exception.ScoreExceptionType;
import com.dabom.score.model.dto.ScoreRegisterReqDto;
import com.dabom.score.model.dto.ScoreUpdateReqDto;
import com.dabom.score.model.entity.Score;
import com.dabom.score.model.entity.ScoreType;
import com.dabom.score.repository.ScoreRepository;
import com.dabom.video.model.Video;
import com.dabom.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScoreService {
    private final ScoreRepository scoreRepository;
    private final MemberRepository memberRepository;
    private final VideoRepository videoRepository;

    @Transactional
    public void register(ScoreRegisterReqDto dto, Integer memberIdx) {
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new ScoreException(ScoreExceptionType.MEMBER_NOT_FOUND));

        ScoreType scoreType = dto.getScoreType();
        if (scoreType == ScoreType.VIDEO) {
            Video video = videoRepository.findById(dto.getVideoIdx())
                    .orElseThrow(() -> new ScoreException(ScoreExceptionType.VIDEO_NOT_FOUND));

            scoreRepository.findByMemberAndVideo(member, video)
                    .ifPresentOrElse(
                            existingScore -> updateScore(existingScore, dto.getScore()),
                            () -> saveNewScore(dto, member, null, video)
                    );
            return;
        }
        if (scoreType == ScoreType.CHANNEL) {
            Member channel = memberRepository.findById(dto.getChannelIdx())
                    .orElseThrow(() -> new ScoreException(ScoreExceptionType.CHANNEL_NOT_FOUND));

            scoreRepository.findByMemberAndChannel(member, channel)
                    .ifPresentOrElse(
                            existingScore -> updateScore(existingScore, dto.getScore()),
                            () -> saveNewScore(dto, member, channel, null)
                    );
            return;
        }

        throw new ScoreException(ScoreExceptionType.SCORE_TYPE_MISMATCH);
    }

    private void updateScore(Score existingScore, double newScore) {
        existingScore.updateScore(newScore);
        scoreRepository.save(existingScore);
    }

    private void saveNewScore(ScoreRegisterReqDto dto, Member member, Member channel, Video video) {
        Score newScore = dto.toEntity(member, channel, video);
        scoreRepository.save(newScore);
    }

    @Transactional
    public void delete(ScoreType type, Integer scoreIdx, Integer memberIdx) {
        Optional<Score> result = scoreRepository.findById(scoreIdx);
        if (result.isPresent()) {
            Score scoreEntity = result.get();
            if (scoreEntity.getScoreType().equals(type) && scoreEntity.getMember().getIdx().equals(memberIdx)) {
                scoreEntity.softDelete();
            } else {
                throw new ScoreException(ScoreExceptionType.SCORE_NOT_FOUND);
            }
        } else {
            throw new ScoreException(ScoreExceptionType.SCORE_NOT_FOUND);
        }
    }

    public Double getAverageScore(ScoreType type, Integer targetIdx) {
        if (type == ScoreType.CHANNEL) {
            return scoreRepository.findAverageScoreByChannelIdx(targetIdx).orElse(0.0);
        } else if (type == ScoreType.VIDEO) {
            return scoreRepository.findAverageScoreByVideoIdx(targetIdx).orElse(0.0);
        }
        else throw new ScoreException(ScoreExceptionType.SCORE_TYPE_MISMATCH);
    }

}
