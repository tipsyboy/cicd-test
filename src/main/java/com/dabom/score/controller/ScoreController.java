package com.dabom.score.controller;

import com.dabom.common.BaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.score.model.dto.ScoreRegisterReqDto;
import com.dabom.score.model.dto.ScoreUpdateReqDto;
import com.dabom.score.model.entity.Score;
import com.dabom.score.model.entity.ScoreType;
import com.dabom.score.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/score")
@RequiredArgsConstructor
public class ScoreController {
    private final ScoreService scoreService;


    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Void>> register(@RequestBody ScoreRegisterReqDto dto,
                                                       @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        Integer memberIdx = memberDetailsDto.getIdx();
        scoreService.register(dto,memberIdx);
        return ResponseEntity.ok(BaseResponse.of(null, HttpStatus.OK));
    }

    @PostMapping("/update")
    public ResponseEntity<BaseResponse<Void>> update(@RequestBody ScoreUpdateReqDto dto) {
        scoreService.update(dto);
        return ResponseEntity.ok(BaseResponse.of(null, HttpStatus.OK));
    }

    @GetMapping("/delete/{type},{idx}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable("type") ScoreType type,
                                                     @PathVariable("idx") Integer Scoreidx,
                                                     @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        scoreService.delete(type,Scoreidx,memberDetailsDto.getIdx());
        return ResponseEntity.ok(BaseResponse.of(null, HttpStatus.OK));

    }

    @GetMapping("/{type}/{idx}/average")
    public ResponseEntity<BaseResponse<Double>> getAverageScore(
            @PathVariable("type") ScoreType type,
            @PathVariable("idx") Integer idx) {
        Double averageScore = scoreService.getAverageScore(type, idx);
        return ResponseEntity.ok(BaseResponse.of(averageScore, HttpStatus.OK));
    }

    @GetMapping("/video/{videoIdx}/user/{memberIdx}")
    public ResponseEntity<BaseResponse<Score>> getUserScoreForVideo(
            @PathVariable("videoIdx") Integer videoIdx,
            @PathVariable("memberIdx") Integer memberIdx) {
        Optional<Score> score = scoreService.getUserScoreForVideo(videoIdx, memberIdx);
        return ResponseEntity.ok(BaseResponse.of(score.orElse(null), HttpStatus.OK));
    }


}
