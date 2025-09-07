package com.dabom.videocomment.model.dto;

import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.videocomment.model.entity.VideoComment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Builder
@Getter
public class VideoCommentResponseDto {
    private Integer idx;
    private String content;
    private String createdAt;
    private String updatedAt;
    private Boolean isModified;
    private Integer likes;
    private Integer likesCount;
    private Boolean isLikes;
    private Integer memberIdx;
    private String username;

    @JsonProperty("profileImg")
    private String profileImg;

    public static VideoCommentResponseDto from(VideoComment entity, String profileImg, MemberDetailsDto memberDetailsDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return VideoCommentResponseDto.builder()
                .idx(entity.getIdx())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt().format(formatter))
                .updatedAt(entity.getUpdatedAt().format(formatter))
                .isModified(!entity.getCreatedAt().equals(entity.getUpdatedAt()))
                .likes(entity.getLikes())
                .likesCount(entity.getLikesCount() != null ? entity.getLikesCount() : 0)
                .isLikes(checkUserLikes(entity, memberDetailsDto))
                .memberIdx(entity.getMember().getIdx())
                .username(entity.getMember().getName())
                .profileImg(profileImg)
                .build();
    }

    private static Boolean checkUserLikes(VideoComment entity, MemberDetailsDto memberDetailsDto) {
        if (memberDetailsDto == null || entity.getLikesList() == null) return false;

        return entity.getLikesList().stream()
                .anyMatch(like -> like.getChannel().getIdx().equals(memberDetailsDto.getIdx()));
    }

}