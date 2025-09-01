package com.dabom.channelboard.controller;

import com.dabom.channelboard.constants.ChannelBoardSwaggerConstants;
import com.dabom.channelboard.model.dto.ChannelBoardReadResponseDto;
import com.dabom.channelboard.model.dto.ChannelBoardRegisterRequestDto;
import com.dabom.channelboard.model.dto.ChannelBoardUpdateRequestDto;
import com.dabom.channelboard.service.ChannelBoardService;
import com.dabom.common.BaseResponse;
import com.dabom.common.SliceBaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Tag(name = "채널 게시판 기능")
@RestController
@RequestMapping("/api/channel/board")
@RequiredArgsConstructor
public class ChannelBoardController {
    private final ChannelBoardService channelBoardService;

    @Operation(
            summary = "게시글 등록",
            description = "새로운 게시글을 등록하는 기능",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "게시글 등록 요청 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChannelBoardRegisterRequestDto.class),
                            examples = @ExampleObject(
                                    name = "게시글 등록 요청 예시",
                                    value = ChannelBoardSwaggerConstants.CHANNEL_BOARD_REGISTER_REQUEST
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "게시글 등록 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "게시글 등록 성공 응답",
                                            value = ChannelBoardSwaggerConstants.CHANNEL_BOARD_REGISTER_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 데이터",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Integer>> register(@RequestBody ChannelBoardRegisterRequestDto dto
            , @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        Integer result = channelBoardService.register(dto, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(result, HttpStatus.OK, "게시글 등록 성공"));
    }

    @Operation(
            summary = "게시글 목록 조회",
            description = "페이지, 사이즈, 정렬 기준을 바탕으로 게시글 목록을 조회합니다. (무한 스크롤 가능)",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호 (기본값 0)", example = "0"),
                    @Parameter(name = "size", description = "페이지당 게시글 수 (기본값 10)", example = "10"),
                    @Parameter(name = "sort", description = "정렬 기준 (기본값 'oldest', 'latest' 가능)", example = "oldest")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "게시글 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SliceBaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "게시글 목록 조회 성공 응답",
                                            value = ChannelBoardSwaggerConstants.CHANNEL_BOARD_LIST_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 페이지, 사이즈 또는 정렬 값",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @GetMapping("/list")
    public ResponseEntity<BaseResponse<SliceBaseResponse<ChannelBoardReadResponseDto>>> list(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "oldest") String sort,
            @RequestParam(required = false) Integer channelIdx,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto
    ) {
        SliceBaseResponse<ChannelBoardReadResponseDto> result = channelBoardService.list(page, size, sort, channelIdx, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(result, HttpStatus.OK, "게시글 목록 조회 성공"));
    }

    @Operation(
            summary = "게시글 상세 조회",
            description = "boardIdx를 이용해 게시글 하나를 조회합니다.",
            parameters = {
                    @Parameter(name = "boardIdx", description = "조회할 게시글 ID", example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "게시글 검색 완료",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChannelBoardReadResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "게시글 조회 성공 응답",
                                            value = ChannelBoardSwaggerConstants.CHANNEL_BOARD_READ_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "게시글을 찾을 수 없음",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @GetMapping("/read/{boardIdx}")
    public ResponseEntity<BaseResponse<ChannelBoardReadResponseDto>> read(@PathVariable Integer boardIdx,
                                                                          @AuthenticationPrincipal MemberDetailsDto memberDetailsDto ) {
        ChannelBoardReadResponseDto result = channelBoardService.read(boardIdx, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(result, HttpStatus.OK, "게시글 검색 완료"));
    }

    @Operation(
            summary = "게시글 수정",
            description = "게시글 내용을 수정합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 게시글 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChannelBoardUpdateRequestDto.class),
                            examples = @ExampleObject(
                                    name = "게시글 수정 요청 예시",
                                    value = ChannelBoardSwaggerConstants.CHANNEL_BOARD_UPDATE_REQUEST
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "게시글 수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "게시글 수정 성공 응답",
                                            value = ChannelBoardSwaggerConstants.CHANNEL_BOARD_UPDATE_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 데이터",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "게시글을 찾을 수 없음",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping("/update")
    public ResponseEntity<BaseResponse<Integer>> update(@RequestBody ChannelBoardUpdateRequestDto dto) {
        Integer result = channelBoardService.update(dto);
        return ResponseEntity.ok(BaseResponse.of(result, HttpStatus.OK, "게시글 수정 성공"));
    }

    @Operation(
            summary = "게시글 삭제",
            description = "boardIdx를 이용해 게시글을 소프트 삭제합니다. (isDeleted 플래그를 true로 설정)",
            parameters = {
                    @Parameter(name = "boardIdx", description = "삭제할 게시글 ID", example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "게시글 삭제 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "게시글 삭제 성공 응답",
                                            value = ChannelBoardSwaggerConstants.CHANNEL_BOARD_DELETE_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "게시글을 찾을 수 없음",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @DeleteMapping("/delete/{boardIdx}")
    public ResponseEntity<Void> delete(@PathVariable Integer boardIdx) {
        channelBoardService.delete(boardIdx);
        return ResponseEntity.noContent().build();
    }
}