package com.dabom.video.controller;

import com.dabom.common.BaseResponse;
import com.dabom.video.constansts.SwaggerConstants;
import com.dabom.video.model.dto.VideoInfoResponseDto;
import com.dabom.video.service.VideoStreamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "비디오 스트리밍 기능", description = "비디오 스트리밍 및 정보 조회 API")
@Slf4j
@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoStreamController {

    private final VideoStreamService videoStreamService;

    @Operation(summary = "비디오 정보 조회", description = "특정 비디오의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비디오 정보 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = SwaggerConstants.VIDEO_INFO_RESPONSE)))
    })
    @GetMapping("/{videoId}")
    public ResponseEntity<BaseResponse<VideoInfoResponseDto>> getVideoInfo(@PathVariable Integer videoId) {
        VideoInfoResponseDto videoInfo = videoStreamService.getVideoInfo(videoId);        return ResponseEntity.ok(BaseResponse.of(videoInfo, HttpStatus.OK));
    }
}
