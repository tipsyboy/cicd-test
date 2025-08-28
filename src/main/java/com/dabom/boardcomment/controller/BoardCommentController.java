package com.dabom.boardcomment.controller;

import com.dabom.boardcomment.model.dto.BoardCommentResponseDto;
import com.dabom.common.BaseResponse;
import com.dabom.boardcomment.model.dto.BoardCommentCreateRequestDto;
import com.dabom.boardcomment.service.BoardCommentService;
import com.dabom.common.SliceBaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
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

import static com.dabom.boardcomment.constansts.SwaggerConstants.*;


@Tag(name = "채널 게시판 댓글 기능")
@RestController
@RequestMapping("/api/channel/board/comment")
@RequiredArgsConstructor
public class BoardCommentController {

    private final BoardCommentService boardCommentService;


    @Operation(
            summary = "댓글 등록",
            description = "게시글에 새로운 댓글을 등록하는 기능",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "댓글 등록 요청 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BoardCommentCreateRequestDto.class),
                            examples = @ExampleObject(
                                    name = "댓글 등록 요청 예시",
                                    value = BOARD_COMMENT_CREATED_REQUEST
                            ))))
    @PostMapping("/create/{boardIdx}")
    @ApiResponse(responseCode = "200", description = "댓글 등록 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BaseResponse.class),
                    examples = @ExampleObject(name = "댓글 등록 성공 응답", value = BOARD_COMMENT_CREATED_RESPONSE
                    )))
    @ApiResponse(responseCode = "400", description = "댓글 등록 실패", content = @Content(mediaType = "application/json"))

    public ResponseEntity<BaseResponse<Integer>> create(@RequestBody BoardCommentCreateRequestDto dto,
                                                        @PathVariable Integer boardIdx,
                                                        @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        Integer idx = boardCommentService.create(dto, boardIdx, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(idx, HttpStatus.OK, "댓글이 성공적으로 등록되었습니다."));
    }

    @Operation(
            summary = "댓글 수정",
            description = "채널 게시글 댓글 수정 기능",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "댓글 수정 요청 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BoardCommentCreateRequestDto.class),
                            examples = @ExampleObject(
                                    name = "댓글 수정 요청 예시",
                                    value = BOARD_COMMENT_UPDATE_REQUEST
                            ))))
    @PutMapping("/update/{boardCommentIdx}")
    @ApiResponse(responseCode = "200", description = "댓글 수정 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BaseResponse.class),
                    examples = @ExampleObject(name = "댓글 수정 성공 응답", value = BOARD_COMMENT_UPDATE_RESPONSE
                    )))
    @ApiResponse(responseCode = "400", description = "댓글 수정 실패", content = @Content(mediaType = "application/json"))

    public ResponseEntity<BaseResponse<BoardCommentResponseDto>> update(
            @RequestBody BoardCommentCreateRequestDto dto,
            @PathVariable Integer boardCommentIdx,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        BoardCommentResponseDto result = boardCommentService.update(boardCommentIdx, dto, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(result, HttpStatus.OK, "댓글 수정 완료"));
    }

    @Operation(
            summary = "댓글 무한 스크롤",
            description = "무한 스크롤을 위한 페이지네이션된 댓글 목록 조회"
    )
    @GetMapping("/list/{boardIdx}/paged")
    @ApiResponse(responseCode = "200", description = "페이지 로딩 완료",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BaseResponse.class),
                    examples = @ExampleObject(name = "페이지 로딩 성공", value = BOARD_COMMENT_PAGING
                    )))
    public ResponseEntity<BaseResponse<SliceBaseResponse<BoardCommentResponseDto>>> getCommentsPaged(
            @PathVariable Integer boardIdx,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "oldest") String sort,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {

        SliceBaseResponse<BoardCommentResponseDto> result =
                boardCommentService.getPagedComments(boardIdx, page, size, sort, memberDetailsDto);

        return ResponseEntity.ok(BaseResponse.of(result, HttpStatus.OK, "댓글 조회 성공"));
    }


    @Operation(
            summary = "댓글 조회(정렬방식 선택)",
            description = "/list/{boardIdx}/lastest(oldest) = 최신순(오래된순)"
    )
    @GetMapping("/list/{boardIdx}/sorted")
    @ApiResponse(responseCode = "200", description = "댓글 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BaseResponse.class),
                    examples = @ExampleObject(name = "댓글 조회 성공 응답", value = BOARD_COMMENT_LIST_RESPONSE
                    )))
    @ApiResponse(responseCode = "400", description = "댓글 조회 실패", content = @Content(mediaType = "application/json"))
    public ResponseEntity<BaseResponse<List<BoardCommentResponseDto>>> list(
            @PathVariable Integer boardIdx,
            @RequestParam(defaultValue = "oldest") String sort,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        List<BoardCommentResponseDto> result = boardCommentService.list(boardIdx, sort, memberDetailsDto);
        return ResponseEntity.ok(BaseResponse.of(result, HttpStatus.OK, "댓글 조회 성공"));
    }


    @Operation(
            summary = " 댓글 삭제",
            description = "채널 게시글의 댓글 삭제하는 기능"
    )
    @DeleteMapping("/delete/{commentIdx}")
    @ApiResponse(responseCode = "200", description = "댓글 삭제 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BaseResponse.class),
                    examples = @ExampleObject(name = "댓글 삭제 성공 응답", value = BOARD_COMMENT_DELETE_RESPONSE
                    )))
    @ApiResponse(responseCode = "400", description = "댓글 삭제 실패", content = @Content(mediaType = "application/json"))
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Integer commentIdx) {
        boardCommentService.delete(commentIdx);
        return ResponseEntity.ok(BaseResponse.of(null,HttpStatus.OK,"댓글 삭제 성공"));
    }
}