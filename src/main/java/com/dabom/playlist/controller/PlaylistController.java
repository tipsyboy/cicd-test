package com.dabom.playlist.controller;

import com.dabom.common.BaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.playlist.model.dto.*;
import com.dabom.playlist.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dabom.playlist.constansts.SwaggerConstants.*;

@Tag(name = "플레이리스트 기능")

@RequestMapping("/api/playlist")
@RestController
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @Operation(
            summary = "플레이리스트 등록",
            description = "새로운 플레이리스트를 등록합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = PLAYLIST_REGISTER_REQUEST)
                    )
            )
    )
    @ApiResponse(responseCode = "200", description = "플레이리스트 등록 성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = PLAYLIST_REGISTER_RESPONSE)
            )
    )
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Integer>> register(
            @RequestBody PlaylistRegisterDto dto,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {

        Integer idx = playlistService.register(dto, memberDetailsDto.getIdx());

        return ResponseEntity.ok(
                BaseResponse.of(idx, HttpStatus.OK, "플레이리스트 등록 되었습니다")
        );
    }

    @Operation(
            summary = "플레이리스트에 영상 추가",
            description = "기존 플레이리스트에 영상을 추가합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = PLAYLIST_ADD_VIDEO_REQUEST)
                    )
            )
    )
    @ApiResponse(responseCode = "200", description = "영상 추가 성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = PLAYLIST_ADD_VIDEO_RESPONSE)
            )
    )
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @ApiResponse(responseCode = "404", description = "플레이리스트 또는 영상을 찾을 수 없음")
    @ApiResponse(responseCode = "409", description = "이미 플레이리스트에 추가된 영상")
    @PostMapping("/add")
    public ResponseEntity<BaseResponse<Void>> add(
            @RequestBody AddVideoDto dto,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {

        playlistService.add(dto, memberDetailsDto.getIdx());

        return ResponseEntity.ok(BaseResponse.of(null, HttpStatus.OK, "영상 추가 완료"));
    }

    @Operation(
            summary = "플레이리스트 목록 조회",
            description = "현재 사용자의 플레이리스트 목록을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "플레이리스트 목록 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = PLAYLIST_LIST_RESPONSE)
            )
    )
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @PostMapping("/list")
    public ResponseEntity<BaseResponse<List<playlistResponseDto>>> list(
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto){

        List<playlistResponseDto> playlists = playlistService.list(memberDetailsDto.getIdx());

        return ResponseEntity.ok(BaseResponse.of(playlists, HttpStatus.OK, "플레이리스트 조회 완료"));
    }

    @Operation(
            summary = "플레이리스트 상세 조회",
            description = "특정 플레이리스트의 상세 정보와 포함된 영상 목록을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "플레이리스트 상세 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = PLAYLIST_DETAILS_RESPONSE)
            )
    )
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @ApiResponse(responseCode = "404", description = "플레이리스트를 찾을 수 없음")
    @GetMapping("/{playlistIdx}")
    public ResponseEntity<BaseResponse<PlaylistInnerDto>> getPlaylistDetails(
            @PathVariable Integer playlistIdx,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {

        PlaylistInnerDto playlistDetails = playlistService.getPlaylistDetails(playlistIdx, memberDetailsDto.getIdx());

        return ResponseEntity.ok(BaseResponse.of(playlistDetails, HttpStatus.OK, "플레이리스트 상세 조회 완료"));
    }

    @Operation(
            summary = "플레이리스트 수정",
            description = "특정 플레이리스트의 제목을 수정합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = PLAYLIST_UPDATE_REQUEST)
                    )
            )
    )
    @ApiResponse(responseCode = "200", description = "플레이리스트 수정 성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = PLAYLIST_UPDATE_RESPONSE)
            )
    )
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @ApiResponse(responseCode = "404", description = "플레이리스트를 찾을 수 없음")
    @PutMapping("/{playlistIdx}")
    public ResponseEntity<BaseResponse<Void>> update(
            @PathVariable Integer playlistIdx,
            @RequestBody PlaylistUpdateDto dto,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {

        playlistService.update(dto, playlistIdx, memberDetailsDto.getIdx());

        return ResponseEntity.ok(BaseResponse.of(null, HttpStatus.OK, "플레이리스트 수정 완료"));
    }

    @Operation(
            summary = "플레이리스트 삭제",
            description = "특정 플레이리스트를 삭제합니다."
    )
    @ApiResponse(responseCode = "200", description = "플레이리스트 삭제 성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = PLAYLIST_DELETE_RESPONSE)
            )
    )
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @ApiResponse(responseCode = "404", description = "플레이리스트를 찾을 수 없음")
    @DeleteMapping("/{playlistIdx}")
    public ResponseEntity<BaseResponse<Void>> delete(
            @PathVariable Integer playlistIdx,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {

        playlistService.delete(playlistIdx, memberDetailsDto.getIdx());

        return ResponseEntity.ok(BaseResponse.of(null, HttpStatus.OK, "플레이리스트 삭제 완료"));
    }
}

