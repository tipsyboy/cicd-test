package com.dabom.video.model;

import com.dabom.common.BaseEntity;
import com.dabom.member.model.entity.Member;
import com.dabom.videocomment.model.entity.VideoComment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Video extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    private String title;
    private String description;
    private boolean isPublic;

    @Enumerated(EnumType.STRING)
    private VideoTag videoTag;

    private String originalFilename;  // 업로드한 원본 파일 이름 (예: user_uploaded.mp4)
    private String originalPath;
    private Long originalSize;

    private String contentType; // MIME 타입 (video/mp4, application/x-mpegURL 등)

    private String savedPath; // 실제 저장된 경로 (로컬 경로 or S3 URL or m3u8 경로)
    private Long savedSize; // 파일 크기 (bytes)

    private Long views; // 영상 조회수

    @Enumerated(EnumType.STRING)
    private VideoStatus videoStatus; // 영상 상태

    private Long totalReviewerCount = 0L; // 총 평가한 사람 수
    private Long totalScore = 0L; // 총 점수
    private double averageScore = 0; // 평점 평균

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    private Member channel;

    @OneToMany(mappedBy = "video", fetch = FetchType.LAZY)
    private List<VideoComment> videoCommentList;


    @Builder
    public Video(String originalFilename, String originalPath, Long originalSize,
                 String contentType, VideoStatus status, Member channel) {
        this.originalFilename = originalFilename;
        this.originalPath = originalPath;
        this.originalSize = originalSize;
        this.contentType = contentType;
        this.videoStatus = status;
        mappingChannel(channel);
    }

    private void mappingChannel(Member channel) {
        this.channel = channel;
        channel.getVideoList().add(this);
    }


    // ===== 비즈니스 로직 =====//
    public void mappingVideoMetadata(String title, String description, boolean isPublic, VideoTag videoTag) {
        this.title = title;
        this.description = description;
        this.isPublic = isPublic;
        this.videoStatus = VideoStatus.ENCODING_PENDING;
        this.videoTag = videoTag;
    }

    public void updateVideoStatus(VideoStatus status) {
        this.videoStatus = status;
    }

    public void updateSavedPath(String savedPath) {
        this.savedPath = savedPath;
    }

    public void addScore(Long newScore) {
        this.totalReviewerCount++;
        this.totalScore += newScore;
        this.averageScore = ((this.averageScore * (this.totalReviewerCount - 1)) + newScore.doubleValue()) / this.totalReviewerCount;
    }

    public void deleteVideo() {
        this.isPublic = false;
    }

    public void incrementViews() {
        this.views = (this.views == null ? 1 : this.views + 1);
    }
}
