package com.dabom.video.controller;

import com.dabom.common.BaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.video.constansts.SwaggerConstants;
import com.dabom.video.model.dto.VideoInformationResponseDto;
import com.dabom.video.service.VideoService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "채널 기능", description = "채널 및 채널의 비디오 관련 API")
@RequestMapping("/api/channels")
@RequiredArgsConstructor
@RestController
public class ChannelController {

    private final VideoService videoService;

    @Operation(summary = "채널 비디오 목록 조회", description = "특정 채널에 업로드된 비디오 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채널 비디오 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = SwaggerConstants.VIDEO_INFO_RESPONSE)))
    })
    @GetMapping("/{channelName}")
    public ResponseEntity<BaseResponse<List<VideoInformationResponseDto>>> getChannelVideoList(@PathVariable String channelName) {
        List<VideoInformationResponseDto> videoList = videoService.getVideoListByChannelName(channelName);

        return ResponseEntity.ok(BaseResponse.of(videoList, HttpStatus.OK));
    }

    @Operation(summary = "내 채널 비디오 목록 조회", description = "로그인한 사용자의 채널에 업로드된 비디오 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내 채널 비디오 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = SwaggerConstants.VIDEO_INFO_RESPONSE)))
    })
    @GetMapping("/videos/manage")
    public ResponseEntity<BaseResponse<List<VideoInformationResponseDto>>> getMyChannelVideoList(@AuthenticationPrincipal MemberDetailsDto loginMember) {
        List<VideoInformationResponseDto> videoList = videoService.getVideoListByLoginMember(loginMember.getIdx());
        return ResponseEntity.ok(BaseResponse.of(videoList, HttpStatus.OK));
    }
}
