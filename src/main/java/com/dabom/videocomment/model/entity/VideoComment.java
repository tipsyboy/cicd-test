package com.dabom.videocomment.model.entity;

import com.dabom.common.BaseEntity;
import com.dabom.likes.model.likes.Likes;
import com.dabom.member.model.entity.Member;
import com.dabom.video.model.Video;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    private String content;
    private Boolean isDeleted;
    private Integer likesCount = 0;

    @Column
    private Integer likes = 0; // 인기순 정렬용

    @ManyToOne
    @JoinColumn(name = "video_idx")
    private Video video;

    @ManyToOne
    @JoinColumn(name = "member_idx")
    private Member member;

    @OneToMany(mappedBy = "videoComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Likes> likesList;

    @Builder
    public VideoComment(String content, Video video, Member member, Boolean isDeleted, Integer likes) {
        this.content = content;
        this.video = video;
        this.member = member;
        this.isDeleted = isDeleted != null ? isDeleted : false;
        this.likes = likes != null ? likes : 0;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public static VideoComment from(VideoComment entity) {
        return VideoComment.builder()
                .content(entity.getContent())
                .video(entity.getVideo())
                .member(entity.getMember())
                .likes(entity.getLikes())
                .build();
    }
    public void decrementLikeCount() {
        this.likesCount = this.likesCount - 1;
    }

    public void incrementLikeCount() {
        this.likesCount = this.likesCount + 1;
    }
}