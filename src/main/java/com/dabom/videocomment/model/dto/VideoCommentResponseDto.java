package com.dabom.videocomment.model.dto;

import com.dabom.member.service.MemberService;
import com.dabom.videocomment.model.entity.VideoComment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class VideoCommentResponseDto {
    private Integer idx;
    private String content;
    private String createdAt;
    private String updatedAt;
    private Boolean isModified;
    private Integer likes;
    private Integer memberIdx; // 프론트엔드에서 삭제 권한 확인용
    private String username; // 프론트엔드 표시용 (Member의 name 필드 매핑)
    @JsonProperty("profileImg")
    private String profileImg;

    public static VideoCommentResponseDto from(VideoComment entity, MemberService memberService) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String profileImgUrl = memberService.getProfileImg(entity.getMember().getIdx());

        return VideoCommentResponseDto.builder()
                .idx(entity.getIdx())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt().format(formatter))
                .updatedAt(entity.getUpdatedAt().format(formatter))
                .isModified(!entity.getCreatedAt().equals(entity.getUpdatedAt()))
                .likes(entity.getLikes())
                .memberIdx(entity.getMember().getIdx())
                .username(entity.getMember().getName())
                .profileImg(profileImgUrl) // S3 presigned URL 사용
                .build();
    }
}