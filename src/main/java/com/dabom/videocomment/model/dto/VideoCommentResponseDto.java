package com.dabom.videocomment.model.dto;

import com.dabom.member.service.MemberService;
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
    private Integer memberIdx;
    private String username;

    @JsonProperty("profileImg")
    private String profileImg;

    public static VideoCommentResponseDto from(VideoComment entity, MemberService memberService) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 안전한 프로필 이미지 로드
        String profileImgUrl = "https://via.placeholder.com/40"; // 기본값

        try {
            if (memberService != null && entity.getMember() != null) {
                String profileImg = memberService.getProfileImg(entity.getMember().getIdx());
                if (profileImg != null && !profileImg.trim().isEmpty()) {
                    profileImgUrl = profileImg;
                }
            }
        } catch (Exception e) {
            System.out.println("댓글 작성자 프로필 이미지 로드 실패 (사용자 ID: " +
                               (entity.getMember() != null ? entity.getMember().getIdx() : "null") +
                               "): " + e.getMessage());
            // 기본값 사용
        }

        return VideoCommentResponseDto.builder()
                .idx(entity.getIdx())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt().format(formatter))
                .updatedAt(entity.getUpdatedAt().format(formatter))
                .isModified(!entity.getCreatedAt().equals(entity.getUpdatedAt()))
                .likes(entity.getLikes())
                .memberIdx(entity.getMember() != null ? entity.getMember().getIdx() : null)
                .username(entity.getMember() != null ? entity.getMember().getName() : "알 수 없음")
                .profileImg(profileImgUrl)
                .build();
    }

    // 기존 메서드와의 호환성을 위해 유지 (deprecated)
    @Deprecated
    public static VideoCommentResponseDto from(VideoComment entity) {
        return from(entity, null);
    }
}