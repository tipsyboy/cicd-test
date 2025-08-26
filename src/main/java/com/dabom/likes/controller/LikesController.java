package com.dabom.likes.controller;

import com.dabom.common.BaseResponse;
import com.dabom.likes.service.LikesService;
import com.dabom.member.security.dto.MemberDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikesController {
    private final LikesService likesService;

    @GetMapping("/{idx}")
    public ResponseEntity<BaseResponse<Void>> likes(
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto,
            @PathVariable Integer idx) {
        likesService.likes(memberDetailsDto, idx);

        return ResponseEntity.ok(BaseResponse.of(null, HttpStatus.MULTI_STATUS));
    }

}

