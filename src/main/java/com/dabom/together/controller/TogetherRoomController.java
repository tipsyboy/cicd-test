package com.dabom.together.controller;

import com.dabom.common.BaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.together.model.dto.request.*;
import com.dabom.together.model.dto.response.TogetherInfoResponseDto;
import com.dabom.together.model.dto.response.TogetherMasterResponseDto;
import com.dabom.together.model.dto.response.TogetherMemberListResponseDto;
import com.dabom.together.service.TogetherJoinMemberService;
import com.dabom.together.service.TogetherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/together/{togetherIdx}")
@RequiredArgsConstructor
public class TogetherRoomController {
    private final TogetherService togetherService;
    private final TogetherJoinMemberService togetherJoinMemberService;

    @PostMapping
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> joinOpenTogether(@PathVariable Integer togetherIdx,
                                                                                  @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherInfoResponseDto responseDto = togetherJoinMemberService.joinNewTogetherMember(togetherIdx, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(responseDto, HttpStatus.OK));
    }

    @GetMapping("/master/member")
    public ResponseEntity<BaseResponse<TogetherMemberListResponseDto>> getTogetherListFromMaster(@PathVariable Integer togetherIdx,
                                                                                                 @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherMemberListResponseDto members = togetherService.getTogetherMembersFromMaster(togetherIdx, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(members, HttpStatus.OK));
    }

    @GetMapping("/master")
    public ResponseEntity<BaseResponse<TogetherMasterResponseDto>> isMaster(@PathVariable Integer togetherIdx,
                                                                            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherMasterResponseDto master = togetherService.isMaster(togetherIdx, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(master, HttpStatus.OK));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> joinTogether(@PathVariable Integer togetherIdx,
                                                                              @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherInfoResponseDto responseDto = togetherJoinMemberService.joinTogetherMember(togetherIdx, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(responseDto, HttpStatus.OK));
    }

    @PatchMapping("/change/title")
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> changeTogetherTitle(@PathVariable Integer togetherIdx,
                                                                                     @RequestBody TogetherChangeTitleRequestDto dto,
                                                                                     @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherInfoResponseDto togetherInfoResponseDto = togetherService.changeTogetherTitle(togetherIdx, dto, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(togetherInfoResponseDto, HttpStatus.OK));
    }

    @PatchMapping("/change/max_number")
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> changeTogetherMaxMemberNumber(@PathVariable Integer togetherIdx,
                                                                                               @RequestBody TogetherChangeMaxMemberRequestDto dto,
                                                                                               @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherInfoResponseDto togetherInfoResponseDto = togetherService.changeMaxMember(togetherIdx, dto, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(togetherInfoResponseDto, HttpStatus.OK));
    }

    @PatchMapping("/change/open")
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> changeTogetherIsOpen(@PathVariable Integer togetherIdx,
                                                                                      @RequestBody TogetherChangeIsOpenRequestDto dto,
                                                                                      @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherInfoResponseDto togetherInfoResponseDto = togetherService.changeIsOpen(togetherIdx, dto, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(togetherInfoResponseDto, HttpStatus.OK));
    }

    @PatchMapping("/change/video")
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> changeTogetherVideo(@PathVariable Integer togetherIdx,
                                                                                     @RequestBody TogetherChangeVideoRequestDto dto,
                                                                                     @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherInfoResponseDto togetherInfoResponseDto = togetherService.changeVideo(togetherIdx, dto, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(togetherInfoResponseDto, HttpStatus.OK));
    }

    @DeleteMapping("/kick")
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> kickMember(@PathVariable Integer togetherIdx,
                                                                            @RequestBody TogetherKickMemberRequestDto dto,
                                                                            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherInfoResponseDto response = togetherService.kickTogetherMember(togetherIdx, dto.getKickedMemberIdx(), memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(response, HttpStatus.OK));
    }

    @DeleteMapping
    public ResponseEntity<BaseResponse<String>> deleteTogether(@PathVariable Integer togetherIdx,
                                                               @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        togetherService.deleteTogether(togetherIdx, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of("투게더가 삭제되었습니다.", HttpStatus.OK));
    }

    @DeleteMapping("/member")
    public ResponseEntity<BaseResponse<String>> deleteTogetherMember(@PathVariable Integer togetherIdx,
                                                                     @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        togetherJoinMemberService.leaveTogetherMember(togetherIdx, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of("멤버가 속한 투게더가 삭제되었습니다.", HttpStatus.OK));
    }
}
