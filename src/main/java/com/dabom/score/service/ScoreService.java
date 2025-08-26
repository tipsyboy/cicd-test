package com.dabom.score.service;

import com.dabom.score.model.dto.ScoreRegisterReqDto;
import com.dabom.score.model.dto.ScoreUpdateReqDto;
import com.dabom.score.model.entity.Score;
import com.dabom.score.model.entity.ScoreType;
import com.dabom.score.repository.ScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScoreService {
    private final ScoreRepository scoreRepository;

    public void register(ScoreRegisterReqDto dto, Integer memberIdx) {
        if (!dto.getMember().getIdx().equals(memberIdx)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "잘못된 접근 입니다.");
        }if (dto.getScoreType() == ScoreType.CHANNEL && scoreRepository.existsByMemberAndChannelAndIsDeletedFalse(dto.getMember(), dto.getChannel())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 해당 채널에 평점을 매겼습니다.");
        }
        if (dto.getScoreType() == ScoreType.VIDEO && scoreRepository.existsByMemberAndVideoAndIsDeletedFalse(dto.getMember(), dto.getVideo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 해당 동영상에 평점을 매겼습니다.");
        }


        scoreRepository.save(dto.toEntity());
    }

    public void update(ScoreUpdateReqDto dto) {
        Optional<Score> result = scoreRepository.findById(dto.getIdx());

        if (result.isPresent()) {
            Score scoreEntity = result.get();
            if (scoreEntity.getScoreType().equals(dto.getScoreType())) {
                scoreEntity.softDelete();
                scoreRepository.save(dto.toEntity());
            }
        }
    }

    public void delete(ScoreType type, Integer scoreIdx, Integer memberIdx) {
        Optional<Score> result = scoreRepository.findById(scoreIdx);
        if (result.isPresent()) {
            Score scoreEntity = result.get();
            if (scoreEntity.getScoreType().equals(type) && scoreEntity.getMember().getIdx().equals(memberIdx)) {
                scoreEntity.softDelete();
            } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Score not found");
        } else throw new RuntimeException("Score not found");

    }
}
