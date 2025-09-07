package com.dabom.videocomment.controller;

import com.dabom.common.BaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.videocomment.model.dto.VideoCommentRegisterDto;
import com.dabom.videocomment.model.dto.VideoCommentResponseDto;
import com.dabom.videocomment.model.entity.VideoComment;
import com.dabom.videocomment.service.VideoCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static com.dabom.videocomment.constansts.SwaggerConstants.*;

@Tag(name = "영상 하단 게시판 기능")
@RequestMapping("/api/videos/comment/")
@RestController
@RequiredArgsConstructor
public class VideoCommentController {

    private final VideoCommentService videoCommentService;

    @Operation(
            summary = "댓글 등록",
            description = "특정 영상에 댓글을 등록한다")
    @ApiResponse(responseCode = "200", description = "댓글 등록 성공")
    @ApiResponse(responseCode = "400", description = "댓글 등록 실패")
    @PostMapping("/register/{videoIdx}")
    public ResponseEntity<BaseResponse<Integer>> register(
            @RequestBody VideoCommentRegisterDto dto,
            @PathVariable Integer videoIdx,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {

        Integer idx = videoCommentService.register(dto, videoIdx, memberDetailsDto.getIdx());

        return ResponseEntity.ok(
                BaseResponse.of(idx, HttpStatus.OK, "댓글이 성공적으로 등록되었습니다.")
        );
    }

    @Operation(
            summary = "댓글 조회(정렬방식 선택)",
            description = "댓글 조회 - 최신순/오래된순/인기순")
    @ApiResponse(responseCode = "200", description = "댓글 조회 성공")
    @ApiResponse(responseCode = "400", description = "댓글 조회 실패")
    @GetMapping("/list/{videoIdx}/paged")
    public ResponseEntity<BaseResponse<Slice<VideoCommentResponseDto>>> list(
            @PathVariable Integer videoIdx,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        Slice<VideoCommentResponseDto> response = videoCommentService.list(videoIdx, pageable, memberDetailsDto);
        return ResponseEntity.ok(
                BaseResponse.of(response, HttpStatus.OK, "댓글 목록 조회 성공")
        );
    }

    @Operation(
            summary = "댓글 삭제",
            description = "특정 댓글을 소프트 삭제")
    @ApiResponse(responseCode = "200", description = "댓글 삭제 성공")
    @ApiResponse(responseCode = "400", description = "댓글 삭제 실패")
    @DeleteMapping("/delete/{commentIdx}")
    public ResponseEntity<BaseResponse<Void>> delete(
            @PathVariable Integer commentIdx,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        VideoComment videoComment = videoCommentService.findById(commentIdx);
        if (!videoComment.getMember().getIdx().equals(memberDetailsDto.getIdx())) {
            throw new SecurityException("본인의 댓글만 삭제할 수 있습니다.");
        }
        videoCommentService.deleted(commentIdx);
        return ResponseEntity.ok(
                BaseResponse.of(null, HttpStatus.OK, "댓글이 성공적으로 삭제되었습니다.")
        );
    }
}