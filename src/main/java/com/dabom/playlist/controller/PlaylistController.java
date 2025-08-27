package com.dabom.playlist.controller;

import com.dabom.common.BaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.playlist.model.dto.AddVideoDto;
import com.dabom.playlist.model.dto.PlaylistRegisterDto;
import com.dabom.playlist.model.dto.playlistResponseDto;
import com.dabom.playlist.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/add")
    public ResponseEntity<BaseResponse<Void>> add(
            @RequestBody AddVideoDto dto,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {

        playlistService.add(dto, memberDetailsDto.getIdx());

        return ResponseEntity.ok(BaseResponse.of(null, HttpStatus.OK, "영상 추가 완료"));
    }

    @PostMapping("/list")
    public ResponseEntity<BaseResponse<List<playlistResponseDto>>> list(
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto){

        List<playlistResponseDto> playlists = playlistService.list(memberDetailsDto.getIdx());

        return ResponseEntity.ok(BaseResponse.of(playlists, HttpStatus.OK, "플레이리스트 조회 완료"));
    }




}
