package com.dabom.video.controller;


import com.dabom.common.BaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.video.model.dto.VideoMetadataRequestDto;
import com.dabom.video.model.dto.score.VideoScoreRequestDto;
import com.dabom.video.service.VideoService;
import com.dabom.video.service.s3.VideoS3EncodingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@Slf4j
@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoMetadataController {

    private final VideoService videoService;
    private final VideoS3EncodingService videoS3EncodingService;
    //    private final VideoLocalEncodingService videoLocalEncodingService;


    @PatchMapping("/metadata/{videoIdx}")
    public ResponseEntity<BaseResponse<Integer>> uploadData(@PathVariable Integer videoIdx,
                                                            @RequestBody VideoMetadataRequestDto requestDto) throws IOException, InterruptedException {

        Integer i = videoService.mappingMetadata(requestDto);

        videoS3EncodingService.encode(videoIdx);

        return ResponseEntity.ok(BaseResponse.of(i, HttpStatus.OK));
    }

    @PostMapping("/{videoIdx}/score")
    public ResponseEntity<BaseResponse<Void>> score(@PathVariable Integer videoIdx,
                                                    @AuthenticationPrincipal MemberDetailsDto loginMember,
                                                    @RequestBody VideoScoreRequestDto requestDto) {

        videoService.scoreVideo(requestDto, loginMember.getIdx(), videoIdx);
        return ResponseEntity.ok(BaseResponse.of(null, HttpStatus.OK, "영상 평가가 완료되었습니다."));
    }

    @DeleteMapping("/metadata/{videoIdx}")
    public ResponseEntity<BaseResponse<Integer>> deleteVideo(@PathVariable Integer videoIdx,
                                                             @AuthenticationPrincipal MemberDetailsDto loginMember) {
        Integer idx = videoService.deleteVideo(videoIdx, loginMember.getIdx());

        return ResponseEntity.ok(BaseResponse.of(idx, HttpStatus.OK));
    }
}