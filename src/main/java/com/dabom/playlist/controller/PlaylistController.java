package com.dabom.playlist.controller;

import com.dabom.common.BaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.playlist.model.dto.AddVideoRequestDto;
import com.dabom.playlist.model.dto.PlaylistRegisterDto;
import com.dabom.playlist.model.entity.Playlist;
import com.dabom.playlist.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/playlist")
@RestController
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Integer>> register(
            @RequestBody PlaylistRegisterDto dto,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {

        Integer idx = playlistService.register(dto, memberDetailsDto.getIdx());

        return ResponseEntity.ok(
                BaseResponse.of(idx, HttpStatus.OK, "플레이리스트 등록 되었습니다")
        );
    }

    @PostMapping("/items")
    public ResponseEntity<BaseResponse<Void>> addVideoToPlaylist(
            @RequestBody AddVideoRequestDto dto,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {

        playlistService.addVideoToPlaylist(dto, memberDetailsDto.getIdx());

        return ResponseEntity.ok(BaseResponse.of(null, HttpStatus.OK, "영상 추가 완료"));
    }



}
