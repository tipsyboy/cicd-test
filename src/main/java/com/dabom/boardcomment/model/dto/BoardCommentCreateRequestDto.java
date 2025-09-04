package com.dabom.boardcomment.model.dto;

import com.dabom.boardcomment.model.entity.BoardComment;
import com.dabom.channelboard.model.entity.ChannelBoard;
import com.dabom.member.model.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class BoardCommentCreateRequestDto {
    @Schema(description = "채널 게시글 댓글", required = true, example = "댓글 내용")
    private String content;


    public BoardComment toEntity(ChannelBoard board, Member member) {
        return BoardComment.builder()
                .content(content)
                .channelBoard(board)
                .channel(member)
                .build();
    }

    public BoardComment toEntity() {
        return BoardComment.builder()
                .content(content)
                .build();
    }
}