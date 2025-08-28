package com.dabom.boardcomment.model.dto;

import com.dabom.boardcomment.model.entity.BoardComment;
import com.dabom.member.security.dto.MemberDetailsDto;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Builder
@Getter
public class BoardCommentResponseDto {
    private Integer idx;
    private String content;
    private String createdAt;
    private String updatedAt;
    private Boolean isModified;
    private Integer likesCount;
    private Boolean isLikes;

    public static BoardCommentResponseDto from(BoardComment entity, MemberDetailsDto memberDetailsDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return BoardCommentResponseDto.builder()
                .idx(entity.getIdx())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt().format(formatter))
                .updatedAt(entity.getUpdatedAt().format(formatter))
                .isModified(!entity.getCreatedAt().equals(entity.getUpdatedAt()))
                .likesCount(entity.getLikesCount())
                .isLikes(checkUserLikes(entity, memberDetailsDto))
                .build();
    }

    // 기존 메서드와의 호환성을 위해 유지 (deprecated)
    @Deprecated
    public static BoardCommentResponseDto from(BoardComment entity) {
        return from(entity, null);
    }

    private static Boolean checkUserLikes(BoardComment entity, MemberDetailsDto memberDetailsDto) {
        if (memberDetailsDto == null || entity.getLikesList() == null) return false;

        return entity.getLikesList().stream()
                .anyMatch(like -> like.getChannel().getIdx().equals(memberDetailsDto.getIdx()));
    }
}