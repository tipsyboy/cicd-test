package com.dabom.video.model;

import com.dabom.common.BaseEntity;
import com.dabom.likes.model.likes.Likes;
import com.dabom.score.model.entity.Score;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private String originalFilename;  // 업로드한 원본 파일 이름 (예: user_uploaded.mp4)
    private String originalPath;
    private Long originalSize;

    private String contentType; // MIME 타입 (video/mp4, application/x-mpegURL 등)

    private String savedPath; // 실제 저장된 경로 (로컬 경로 or S3 URL or m3u8 경로)
    private Long savedSize; // 파일 크기 (bytes)
    private Integer likesCount;

    @Enumerated(EnumType.STRING)
    private VideoStatus videoStatus; // 영상 상태

    private Long totalRatingCount = 0L; // 총 평가한 사람 수
    private Long totalScore = 0L; // 총 점수
    private double averageScore = 0; // 평점 평균
//    @OneToMany(mappedBy = "video", fetch = FetchType.LAZY)
//    private List<Score> scoresList;     // 평점 리스트


    @Builder
    public Video(String originalFilename, String originalPath, Long originalSize, String contentType, VideoStatus status) {
        this.originalFilename = originalFilename;
        this.originalPath = originalPath;
        this.originalSize = originalSize;
        this.contentType = contentType;
        this.videoStatus = status;
    }

    public void updateVideoStatus(VideoStatus status) {
        this.videoStatus = status;
    }

    public void mappingVideoMetadata(String title, String description, boolean isPublic) {
        this.title = title;
        this.description = description;
        this.isPublic = isPublic;
        this.videoStatus = VideoStatus.ENCODING_PENDING;
    }

    public void updateSavedPath(String savedPath) {
        this.savedPath = savedPath;
    }

    public void addScore(Long newScore) {
        this.totalRatingCount++;
        this.totalScore += newScore;
        this.averageScore = ((this.averageScore * (this.totalRatingCount - 1)) + newScore.doubleValue()) / this.totalRatingCount;
    }
}
