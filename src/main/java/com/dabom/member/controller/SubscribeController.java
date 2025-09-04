package com.dabom.member.controller;

import com.dabom.common.BaseResponse;
import com.dabom.member.model.dto.request.MemberSubscribeRequestDto;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.member.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscribe")
@RequiredArgsConstructor
public class SubscribeController {
    private final SubscribeService subscribeService;

    @PostMapping("/save")
    public ResponseEntity<BaseResponse<String>> subscribe(@RequestBody MemberSubscribeRequestDto dto,
                                                          @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        subscribeService.subscribe(dto.getMemberIdx(), memberDetailsDto.getIdx());
        return ResponseEntity.ok(BaseResponse.of("구독 성공", HttpStatus.OK));
    }

    @PostMapping("/delete")
    public ResponseEntity<BaseResponse<String>> subscribeDelete(@RequestBody MemberSubscribeRequestDto dto,
                                                                @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        subscribeService.subscribeDelete(dto.getMemberIdx(), memberDetailsDto.getIdx());
        return ResponseEntity.ok(BaseResponse.of("구독 취소", HttpStatus.OK));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<Boolean>> getSubscribe(@RequestBody MemberSubscribeRequestDto dto,
                                                     @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        Boolean subscribe = subscribeService.getSubscribe(dto.getMemberIdx(), memberDetailsDto.getIdx());
        return ResponseEntity.ok(BaseResponse.of(subscribe, HttpStatus.OK));
    }
}
