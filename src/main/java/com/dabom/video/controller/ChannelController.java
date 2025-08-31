package com.dabom.video.controller;

import com.dabom.common.BaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.video.model.dto.VideoInformationResponseDto;
import com.dabom.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/channels")
@RequiredArgsConstructor
@RestController
public class ChannelController {

    private final VideoService videoService;

    @GetMapping("/{channelName}")
    public ResponseEntity<BaseResponse<List<VideoInformationResponseDto>>> getChannelVideoList(@PathVariable String channelName) {
        List<VideoInformationResponseDto> videoList = videoService.getVideoListByChannelName(channelName);

        return ResponseEntity.ok(BaseResponse.of(videoList, HttpStatus.OK));
    }

    @GetMapping("/videos/manage")
    public ResponseEntity<BaseResponse<List<VideoInformationResponseDto>>> getMyChannelVideoList(@AuthenticationPrincipal MemberDetailsDto loginMember) {
        List<VideoInformationResponseDto> videoList = videoService.getVideoListByLoginMember(loginMember.getIdx());
        return ResponseEntity.ok(BaseResponse.of(videoList, HttpStatus.OK));
    }
}
