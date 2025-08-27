package com.dabom.channelboard.model.dto;

import com.dabom.channelboard.model.entity.ChannelBoard;
import com.dabom.member.security.dto.MemberDetailsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class ChannelBoardReadResponseDto {
    @Schema(description = "게시글 고유 ID", example = "1")
    private Integer idx;

    @Schema(description = "게시글 제목", example = "첫 번째 공지사항")
    private String title;

    @Schema(description = "게시글 내용", example = "안녕하세요. 채널에 오신 것을 환영합니다!")
    private String contents;

    @Schema(description = "게시글 생성일", example = "2025-08-18 15:30:00")
    private String createdAt;

    @Schema(description = "댓글 개수", example = "5")
    private Integer commentCount;

    private Integer likesCount;

    private Boolean isLikes;

    public static ChannelBoardReadResponseDto from(ChannelBoard entity, MemberDetailsDto memberDetailsDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return ChannelBoardReadResponseDto.builder()
                .idx(entity.getIdx())
                .title(entity.getTitle())
                .contents(entity.getContents())
                .createdAt(entity.getCreatedAt().format(formatter))
                .likesCount(entity.getLikesCount())
                .commentCount(0)
                .isLikes(checkUserLikes(entity, memberDetailsDto))
                .build();
    }

    private static Boolean checkUserLikes(ChannelBoard entity, MemberDetailsDto memberDetailsDto) {
        if (memberDetailsDto == null || entity.getLikesList() == null) return false;

        return entity.getLikesList().stream()
                .anyMatch(like -> like.getChannel().getIdx().equals(memberDetailsDto.getIdx()));
    }

    public static ChannelBoardReadResponseDto fromWithCommentCount(ChannelBoard entity, Long commentCount, MemberDetailsDto memberDetailsDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return ChannelBoardReadResponseDto.builder()
                .idx(entity.getIdx())
                .title(entity.getTitle())
                .contents(entity.getContents())
                .createdAt(entity.getCreatedAt().format(formatter))
                .likesCount(entity.getLikesCount())
                .commentCount(commentCount.intValue())
                .isLikes(checkUserLikes(entity, memberDetailsDto))
                .build();
    }
}