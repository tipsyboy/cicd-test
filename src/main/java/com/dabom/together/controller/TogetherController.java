package com.dabom.together.controller;

import com.dabom.common.BaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.together.constansts.SwaggerConstants;
import com.dabom.together.exception.TogetherException;
import com.dabom.together.model.dto.request.*;
import com.dabom.together.model.dto.response.TogetherInfoResponseDto;
import com.dabom.together.model.dto.response.TogetherListResponseDto;
import com.dabom.together.model.dto.response.TogetherMasterResponseDto;
import com.dabom.together.model.dto.response.TogetherMemberListResponseDto;
import com.dabom.together.service.TogetherJoinMemberService;
import com.dabom.together.service.TogetherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.dabom.together.exception.TogetherExceptionType.CHOICE_OPEN_STATUS;

@Tag(name = "같이보기 기능", description = "같이보기 관련 API")
@RestController
@RequestMapping("/api/together")
@RequiredArgsConstructor
public class TogetherController {
    private final TogetherService togetherService;
    private final TogetherJoinMemberService togetherJoinMemberService;

    @Operation(summary = "같이보기방 생성", description = "새로운 같이보기방을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "같이보기방 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = SwaggerConstants.TOGETHER_CREATE_RESPONSE)))
    })
    @PostMapping("/save")
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> saveTogether(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "같이보기방 생성 정보",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TogetherCreateRequestDto.class),
                            examples = @ExampleObject(value = SwaggerConstants.TOGETHER_CREATE_REQUEST)
                    )
            )
            @RequestBody TogetherCreateRequestDto dto,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        if(dto.getIsOpen() == null) {
            throw new TogetherException(CHOICE_OPEN_STATUS);
        }
        TogetherInfoResponseDto togetherDto = togetherService.createTogether(dto, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(togetherDto, HttpStatus.OK));
    }

    @Operation(summary = "같이보기방 목록 조회", description = "현재 활성화된 같이보기방 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "같이보기방 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = SwaggerConstants.TOGETHER_LIST_RESPONSE)))
    })
    @GetMapping
    public ResponseEntity<BaseResponse<TogetherListResponseDto>> getTogetherList(@RequestParam(defaultValue = "0") Integer page,
                                                                                 @RequestParam(defaultValue = "10") Integer size) {
        TogetherListResponseDto togetherListResponseDto = togetherService.getTogetherListTest(page, size);
        return ResponseEntity.ok(BaseResponse.of(togetherListResponseDto, HttpStatus.OK));
    }

    @Operation(summary = "참여중인 같이보기방 목록 조회", description = "현재 사용자가 참여하고 있는 같이보기방 목록을 조회합니다.")
    @GetMapping("/member")
    public ResponseEntity<BaseResponse<TogetherListResponseDto>> getTogetherListInMember(@AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherListResponseDto togetherListResponseDto = togetherJoinMemberService.getTogethersFromMember(memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(togetherListResponseDto, HttpStatus.OK));
    }

    @Operation(summary = "내가 만든 같이보기방 목록 조회", description = "현재 사용자가 방장인 같이보기방 목록을 조회합니다.")
    @GetMapping("/my_room")
    public ResponseEntity<BaseResponse<TogetherListResponseDto>> getTogetherListInMaster(@AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherListResponseDto togetherListResponseDto = togetherJoinMemberService.getTogethersFromMaster(memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(togetherListResponseDto, HttpStatus.OK));
    }

    @Operation(summary = "같이보기방 검색", description = "키워드로 같이보기방을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<TogetherListResponseDto>> searchTogethers(@RequestParam String search,
                                                                                 @RequestParam(defaultValue = "0") Integer page,
                                                                                 @RequestParam(defaultValue = "10") Integer size) {
        TogetherListResponseDto togetherListResponseDto = togetherService.searchTogetherList(search, page, size);
        return ResponseEntity.ok(BaseResponse.of(togetherListResponseDto, HttpStatus.OK));
    }

    @Operation(summary = "코드로 같이보기방 참여", description = "참여 코드를 사용하여 비공개 같이보기방에 참여합니다.")
    @PostMapping("/code")
    public ResponseEntity<BaseResponse<TogetherInfoResponseDto>> joinTogetherWithCode(@RequestBody TogetherJoinWithCodeRequestDto dto,
                                                               @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        TogetherInfoResponseDto responseDto =
                togetherJoinMemberService.joinTogetherWithCodeMember(dto, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(responseDto, HttpStatus.OK));
    }
}
