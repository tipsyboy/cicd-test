package com.dabom.search.model.dto;

import com.dabom.member.service.MemberService;
import com.dabom.s3.S3UrlBuilder;
import com.dabom.video.model.Video;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Builder
public class SearchResponseDto {
    private Integer videoId;
    private String title;
    private String description;
    //    private String duration;
    private String views;
    private Double rating;
    private Boolean isPublic;
    private Integer uploadedAt;
    private ChannelInfo channel;
    private String videoImage;


    public static SearchResponseDto from(Video video, MemberService memberService, S3UrlBuilder s3UrlBuilder) {
        String channelProfileImg = "https://via.placeholder.com/40"; // 기본값
        String thumbnail = "https://via.placeholder.com/40"; // 기본값

        try {
            if (memberService != null && video.getChannel() != null) {
                String profileImg = memberService.getProfileImg(video.getChannel().getIdx());
                if (profileImg != null && !profileImg.trim().isEmpty()) {
                    channelProfileImg = profileImg;
                }
            }

            if (s3UrlBuilder != null && video.getThumbnailImage().getSavedPath() != null) {
                String savedPath = video.getThumbnailImage().getSavedPath();
                if (!savedPath.trim().isEmpty()) {
                    thumbnail = s3UrlBuilder.buildPublicUrl(savedPath);
                }
            }
        } catch (Exception e) {
            System.out.println("오류테스트");
        }

        return SearchResponseDto.builder()
                .videoId(video.getIdx())
                .title(video.getTitle())
                .description(video.getDescription())
                .isPublic(video.isPublic())
                .videoImage(thumbnail)
                .uploadedAt(calculateDaysAgo(video.getCreatedAt()))
                .channel(ChannelInfo.builder()
                        .idx(video.getChannel() != null ? video.getChannel().getIdx() : null)
                        .name(video.getChannel() != null ? video.getChannel().getName() : "알 수 없음")
                        .content(video.getChannel() != null ? video.getChannel().getContent() : null)
                        .profileImg(channelProfileImg)
                        .build())
                .build();
    }


    private static Integer calculateDaysAgo(LocalDateTime createdAt) {
        if (createdAt == null) return 0;
        return (int) ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
    }

    @Getter
    @Builder
    public static class ChannelInfo {
        private Integer idx;
        private String name;
        private String content;
        private String profileImg;
    }

}