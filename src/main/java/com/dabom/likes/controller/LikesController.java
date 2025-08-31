package com.dabom.likes.controller;

import com.dabom.common.BaseResponse;
import com.dabom.likes.service.LikesService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.dabom.likes.constansts.SwaggerConstants.LIKE_TOGGLE_RESPONSE;


@Tag(name = "좋아요 관리 기능")
@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikesController {
    private final LikesService likesService;

    @Operation(
            summary = "게시글 댓글 좋아요 처리",
            description = "게시글 댓글에 좋아요를 추가하거나 취소합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "댓글 좋아요 처리 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "댓글 좋아요 처리 성공 응답",
                                            value = LIKE_TOGGLE_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "존재하지 않는 댓글에 대한 요청",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않은 사용자의 요청",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @GetMapping("/boardComment/{idx}")
    public ResponseEntity<BaseResponse<Void>> handleBoardCommentLike(
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto,
            @PathVariable Integer idx) {

        likesService.handleBoardCommentLike(memberDetailsDto, idx);

        return ResponseEntity.ok(BaseResponse.of(null, HttpStatus.OK));
    }

    @Operation(
            summary = "채널 게시글 좋아요 처리",
            description = "채널 게시글에 좋아요를 추가하거나 취소합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "채널 게시글 좋아요 처리 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class),
                                    examples = @ExampleObject(
                                            name = "채널 게시글 좋아요 처리 성공 응답",
                                            value = LIKE_TOGGLE_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "존재하지 않는 게시글에 대한 요청",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않은 사용자의 요청",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @GetMapping("/channelBoard/{idx}")
    public ResponseEntity handleChannelBoardLike(
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto,
            @PathVariable Integer idx) {
        likesService.handleChannelBoardLike(memberDetailsDto, idx);

        return ResponseEntity.ok(true);
    }
}