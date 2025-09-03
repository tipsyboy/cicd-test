package com.dabom.score.service;

import com.dabom.score.exception.ScoreException;
import com.dabom.score.exception.ScoreExceptionType;
import com.dabom.score.model.dto.ScoreRegisterReqDto;
import com.dabom.score.model.dto.ScoreUpdateReqDto;
import com.dabom.score.model.entity.Score;
import com.dabom.score.model.entity.ScoreType;
import com.dabom.score.repository.ScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScoreService {
    private final ScoreRepository scoreRepository;

    @Transactional
    public void register(ScoreRegisterReqDto dto, Integer memberIdx) {
        if (!dto.getMember().getIdx().equals(memberIdx)) {
            throw new ScoreException(ScoreExceptionType.INVALID_ACCESS);
        }
        if (dto.getScoreType() == ScoreType.CHANNEL && scoreRepository.existsByMemberAndChannelAndIsDeletedFalse(dto.getMember(), dto.getChannel())) {
            throw new ScoreException(ScoreExceptionType.ALREADY_RATED_CHANNEL);
        }
        if (dto.getScoreType() == ScoreType.VIDEO && scoreRepository.existsByMemberAndVideoAndIsDeletedFalse(dto.getMember(), dto.getVideo())) {
            throw new ScoreException(ScoreExceptionType.ALREADY_RATED_VIDEO);
        }

        scoreRepository.save(dto.toEntity());
    }

    @Transactional
    public void update(ScoreUpdateReqDto dto) {
        Optional<Score> result = scoreRepository.findById(dto.getIdx());

        if (result.isPresent()) {
            Score scoreEntity = result.get();
            if (scoreEntity.getScoreType().equals(dto.getScoreType())) {
                scoreEntity.softDelete();
                scoreRepository.save(dto.toEntity());
            } else {
                throw new ScoreException(ScoreExceptionType.SCORE_TYPE_MISMATCH);
            }
        } else {
            throw new ScoreException(ScoreExceptionType.SCORE_NOT_FOUND);
        }
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
        return scoreRepository.findAverageScoreByScoreTypeAndTargetIdx(type, targetIdx)
                .orElse(0.0); // 평점이 없을 경우 0.0 반환
    }

    public Optional<Score> getUserScoreForVideo(Integer videoIdx, Integer memberIdx) {
        return scoreRepository.findByMemberIdxAndVideoIdxAndIsDeletedFalse(memberIdx, videoIdx);
    }
}
