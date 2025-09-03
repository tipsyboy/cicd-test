package com.dabom.together.controller;

import com.dabom.common.BaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.together.exception.TogetherException;
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

import static com.dabom.together.exception.TogetherExceptionType.CHOICE_OPEN_STATUS;

@RestController
@RequestMapping("/api/together")
@RequiredArgsConstructor
public class TogetherController {
    private final TogetherService togetherService;
    private final TogetherJoinMemberService togetherJoinMemberService;

    @PostMapping("/save")
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> saveTogether(@RequestBody TogetherCreateRequestDto dto,
                                         @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        if(dto.getIsOpen() == null) {
            throw new TogetherException(CHOICE_OPEN_STATUS);
        }
        TogetherInfoResponseDto togetherDto = togetherService.createTogether(dto, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(togetherDto, HttpStatus.OK));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<TogetherListResponseDto>> getTogetherList(@RequestParam(defaultValue = "0") Integer page,
                                                                                 @RequestParam(defaultValue = "10") Integer size) {
        TogetherListResponseDto togetherListResponseDto = togetherService.getTogetherListTest(page, size);
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

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<TogetherListResponseDto>> searchTogethers(@RequestParam String search,
                                                                                 @RequestParam(defaultValue = "0") Integer page,
                                                                                 @RequestParam(defaultValue = "10") Integer size) {
        TogetherListResponseDto togetherListResponseDto = togetherService.searchTogetherList(search, page, size);
        return ResponseEntity.ok(BaseResponse.of(togetherListResponseDto, HttpStatus.OK));
    }

    @PostMapping("/code")
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> joinTogetherWithCode(@RequestBody TogetherJoinWithCodeRequestDto dto,
                                                               @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherInfoResponseDto responseDto =
                togetherJoinMemberService.joinTogetherWithCodeMember(dto, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(responseDto, HttpStatus.OK));
    }
}
