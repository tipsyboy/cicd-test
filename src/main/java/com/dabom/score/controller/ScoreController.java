package com.dabom.score.controller;

import com.dabom.common.BaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.score.constansts.SwaggerConstants;
import com.dabom.score.model.dto.ScoreApiRequestDto;
import com.dabom.score.model.dto.ScoreRegisterReqDto;
import com.dabom.score.model.dto.ScoreUpdateReqDto;
import com.dabom.score.model.entity.Score;
import com.dabom.score.model.entity.ScoreType;
import com.dabom.score.service.ScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "점수 기능", description = "콘텐츠 점수 관련 API")
@RestController
@RequestMapping("/api/score")
@RequiredArgsConstructor
public class ScoreController {
    private final ScoreService scoreService;


    @Operation(summary = "점수 등록", description = "특정 콘텐츠에 대한 점수를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "점수 등록 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = SwaggerConstants.SCORE_REGISTER_RESPONSE)))
    })
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Void>> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "점수 정보",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScoreRegisterReqDto.class),
                            examples = @ExampleObject(value = SwaggerConstants.SCORE_REGISTER_REQUEST)
                    )
            )
            @RequestBody ScoreRegisterReqDto dto,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        Integer memberIdx = memberDetailsDto.getIdx();
        scoreService.register(dto,memberIdx);
        return ResponseEntity.ok(BaseResponse.of(null, HttpStatus.OK,"등록 성공!"));
    }

    @Operation(summary = "점수 삭제", description = "특정 콘텐츠에 대한 점수를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "점수 삭제 성공")
    })
    @GetMapping("/delete/{type},{idx}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable("type") ScoreType type,
                                                     @PathVariable("idx") Integer Scoreidx,
                                                     @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        scoreService.delete(type,Scoreidx,memberDetailsDto.getIdx());
        return ResponseEntity.ok(BaseResponse.of(null, HttpStatus.OK));

    }

    @Operation(summary = "평균 점수 조회", description = "특정 콘텐츠의 평균 점수를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "평균 점수 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = SwaggerConstants.GET_AVG_SCORE_RESPONSE)))
    })
    @GetMapping("/{type}/{idx}/average")
    public ResponseEntity<BaseResponse<Double>> getAverageScore(
            @PathVariable("type") ScoreType type,
            @PathVariable("idx") Integer idx) {
        Double averageScore = scoreService.getAverageScore(type, idx);
        return ResponseEntity.ok(BaseResponse.of(averageScore, HttpStatus.OK));
    }

}
