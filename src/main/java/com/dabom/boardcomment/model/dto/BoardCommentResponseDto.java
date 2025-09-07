package com.dabom.boardcomment.model.dto;

import com.dabom.boardcomment.model.entity.BoardComment;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.member.service.MemberService;
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
    private String name;
    private String profileImg;

    public static BoardCommentResponseDto from(BoardComment entity, MemberDetailsDto memberDetailsDto, MemberService memberService) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String profileImgUrl = "https://via.placeholder.com/32";
        if (memberService != null) {
            profileImgUrl = memberService.getProfileImg(entity.getChannel().getIdx());
        }

        return BoardCommentResponseDto.builder()
                .idx(entity.getIdx())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt().format(formatter))
                .updatedAt(entity.getUpdatedAt().format(formatter))
                .isModified(!entity.getCreatedAt().equals(entity.getUpdatedAt()))
                .likesCount(entity.getLikesCount())
                .isLikes(checkUserLikes(entity, memberDetailsDto))
                .name(entity.getChannel().getName())
                .profileImg(profileImgUrl)
                .build();
    }

    @Deprecated
    public static BoardCommentResponseDto from(BoardComment entity, MemberDetailsDto memberDetailsDto) {
        return from(entity, memberDetailsDto, null); // memberService를 null로 전달
    }

    @Deprecated
    public static BoardCommentResponseDto from(BoardComment entity) {
        return from(entity, null, null); // 모든 파라미터를 null로 전달
    }

    private static Boolean checkUserLikes(BoardComment entity, MemberDetailsDto memberDetailsDto) {
        if (memberDetailsDto == null || entity.getLikesList() == null) return false;

        return entity.getLikesList().stream()
                .anyMatch(like -> like.getChannel().getIdx().equals(memberDetailsDto.getIdx()));
    }
}