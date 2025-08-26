package com.dabom.video.controller;

import com.dabom.common.BaseResponse;
import com.dabom.video.model.dto.VideoInfoResponseDto;
import com.dabom.video.service.VideoStreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoStreamController {

    private final VideoStreamService videoStreamService;

    @GetMapping("/{videoId}")
    public ResponseEntity<BaseResponse<VideoInfoResponseDto>> getVideoInfo(@PathVariable Integer videoId) {
        VideoInfoResponseDto videoInfo = videoStreamService.getVideoInfo(videoId);
        return ResponseEntity.ok(BaseResponse.of(videoInfo, HttpStatus.OK));
    }
}
