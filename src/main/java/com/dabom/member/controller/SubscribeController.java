package com.dabom.member.controller;

import com.dabom.common.BaseResponse;
import com.dabom.member.model.dto.request.MemberSubscribeRequestDto;
import com.dabom.member.model.dto.request.SubscribeCreateDto;
import com.dabom.member.model.dto.response.MemberInfoResponseDto;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.member.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class SubscribeController {
    private final SubscribeService subscribeService;

    @PostMapping("/subscribe/{channelIdx}")
    public ResponseEntity<BaseResponse<String>> createSubscribe(
            @AuthenticationPrincipal MemberDetailsDto memberdto,
            @PathVariable Integer channelIdx,
            @RequestBody SubscribeCreateDto dto) {

        subscribeService.create(dto, memberdto.getIdx(), channelIdx);

        return ResponseEntity.ok(BaseResponse.of("구독 성공", HttpStatus.OK));
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<BaseResponse<List<MemberInfoResponseDto>>> subList(
            @AuthenticationPrincipal MemberDetailsDto dto) {
        List<MemberInfoResponseDto> subscribedChannels = subscribeService.sublist(dto.getIdx());

        return ResponseEntity.ok(BaseResponse.of(subscribedChannels, HttpStatus.OK));

    }

    @DeleteMapping("/unsubscribe/{channelIdx}")
    public ResponseEntity<BaseResponse<String>> unsubscribeChannel(
            @AuthenticationPrincipal MemberDetailsDto memberdto,
            @PathVariable Integer channelIdx) {

        subscribeService.unsubscribe(memberdto.getIdx(), channelIdx);

        return ResponseEntity.ok(BaseResponse.of("채널 구독 취소 성공", HttpStatus.OK));
    }
}
