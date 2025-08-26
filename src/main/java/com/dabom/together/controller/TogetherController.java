package com.dabom.together.controller;

import com.dabom.common.BaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.together.model.dto.request.*;
import com.dabom.together.model.dto.response.TogetherInfoResponseDto;
import com.dabom.together.model.dto.response.TogetherListResponseDto;
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
@RequestMapping("/api/together")
@RequiredArgsConstructor
public class TogetherController {
    private final TogetherService togetherService;
    private final TogetherJoinMemberService togetherJoinMemberService;

    @PostMapping("/save")
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> saveTogether(@RequestBody TogetherCreateRequestDto dto,
                                         @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherInfoResponseDto togetherDto = togetherService.createTogether(dto, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(togetherDto, HttpStatus.OK));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<TogetherListResponseDto>> getTogetherList() {
        TogetherListResponseDto togetherListResponseDto = togetherService.getTogetherList();
        return ResponseEntity.ok(BaseResponse.of(togetherListResponseDto, HttpStatus.OK));
    }

    @GetMapping("/member")
    public ResponseEntity<BaseResponse<TogetherListResponseDto>> getTogetherListInMember(@AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherListResponseDto togetherListResponseDto = togetherJoinMemberService.getTogethersFromMember(memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(togetherListResponseDto, HttpStatus.OK));
    }

    @GetMapping("/my_room")
    public ResponseEntity<BaseResponse<TogetherListResponseDto>> getTogetherListInMaster(@AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherListResponseDto togetherListResponseDto = togetherJoinMemberService.getTogethersFromMaster(memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(togetherListResponseDto, HttpStatus.OK));
    }

    @PostMapping("/search")
    public ResponseEntity<BaseResponse<TogetherListResponseDto>> searchTogethers(@RequestBody TogetherSearchRequestDto dto) {
        TogetherListResponseDto togetherListResponseDto = togetherService.searchTogetherList(dto);
        return ResponseEntity.ok(BaseResponse.of(togetherListResponseDto, HttpStatus.OK));
    }

    @PostMapping("{togetherIdx}/code")
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> joinTogetherWithCode(@PathVariable Integer togetherIdx,
                                                               @RequestBody TogetherJoinWithCodeRequestDto dto,
                                                               @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherInfoResponseDto responseDto =
                togetherJoinMemberService.joinTogetherWithCodeMember(togetherIdx, dto, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(responseDto, HttpStatus.OK));
    }

    @PostMapping("/{togetherIdx}")
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> joinOpenTogether(@PathVariable Integer togetherIdx,
                                                           @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherInfoResponseDto responseDto = togetherJoinMemberService.joinNewTogetherMember(togetherIdx, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(responseDto, HttpStatus.OK));
    }

    @GetMapping("/{togetherIdx}/master/member")
    public ResponseEntity<BaseResponse<TogetherMemberListResponseDto>> getTogetherListFromMaster(@PathVariable Integer togetherIdx,
                                                                              @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherMemberListResponseDto members = togetherService.getTogetherMembersFromMaster(togetherIdx, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(members, HttpStatus.OK));
    }

    @GetMapping("/{togetherIdx}/master")
    public ResponseEntity<BaseResponse<TogetherMasterResponseDto>> isMaster(@PathVariable Integer togetherIdx,
                                                                                @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherMasterResponseDto master = togetherService.isMaster(togetherIdx, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(master, HttpStatus.OK));
    }

    @GetMapping("/{togetherIdx}")
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> joinTogether(@PathVariable Integer togetherIdx,
                                                       @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherInfoResponseDto responseDto = togetherJoinMemberService.joinTogetherMember(togetherIdx, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(responseDto, HttpStatus.OK));
    }

    @PatchMapping("/{togetherIdx}/change/title")
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> changeTogetherTitle(@PathVariable Integer togetherIdx,
                                                              @RequestBody TogetherChangeTitleRequestDto dto,
                                                              @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherInfoResponseDto togetherInfoResponseDto = togetherService.changeTogetherTitle(togetherIdx, dto, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(togetherInfoResponseDto, HttpStatus.OK));
    }

    @PatchMapping("/{togetherIdx}/change/max_number")
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> changeTogetherMaxMemberNumber(@PathVariable Integer togetherIdx,
                                                                                     @RequestBody TogetherChangeMaxMemberRequestDto dto,
                                                                                     @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherInfoResponseDto togetherInfoResponseDto = togetherService.changeMaxMember(togetherIdx, dto, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(togetherInfoResponseDto, HttpStatus.OK));
    }

    @PatchMapping("/{togetherIdx}/change/open")
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> changeTogetherIsOpen(@PathVariable Integer togetherIdx,
                                                                                      @RequestBody TogetherChangeIsOpenRequestDto dto,
                                                                                      @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherInfoResponseDto togetherInfoResponseDto = togetherService.changeIsOpen(togetherIdx, dto, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(togetherInfoResponseDto, HttpStatus.OK));
    }

    @PatchMapping("/{togetherIdx}/change/video")
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> changeTogetherVideo(@PathVariable Integer togetherIdx,
                                                                                      @RequestBody TogetherChangeVideoRequestDto dto,
                                                                                      @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherInfoResponseDto togetherInfoResponseDto = togetherService.changeVideo(togetherIdx, dto, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(togetherInfoResponseDto, HttpStatus.OK));
    }

    @DeleteMapping("/{togetherIdx}/kick")
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> kickMember(@PathVariable Integer togetherIdx,
                                                                            @RequestBody TogetherKickMemberRequestDto dto,
                                                     @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherInfoResponseDto response = togetherService.kickTogetherMember(togetherIdx, dto.getKickedMemberIdx(), memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(response, HttpStatus.OK));
    }

    @DeleteMapping("/{togetherIdx}")
    public ResponseEntity<BaseResponse<String>> deleteTogether(@PathVariable Integer togetherIdx,
                                                               @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        togetherService.deleteTogether(togetherIdx, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of("투게더가 삭제되었습니다.", HttpStatus.OK));
    }

    @DeleteMapping("/{togetherIdx}/member")
    public ResponseEntity<BaseResponse<String>> deleteTogetherMember(@PathVariable Integer togetherIdx,
                                                               @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        togetherJoinMemberService.leaveTogetherMember(togetherIdx, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of("멤버가 속한 투게더가 삭제되었습니다.", HttpStatus.OK));
    }
}
