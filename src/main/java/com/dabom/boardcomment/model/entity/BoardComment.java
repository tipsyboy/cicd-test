package com.dabom.boardcomment.model.entity;

import com.dabom.channelboard.model.entity.ChannelBoard;
import com.dabom.common.BaseEntity;
import com.dabom.likes.model.likes.Likes;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;
    private String content;
    private Boolean isDeleted;
    private Integer likesCount;

    @ManyToOne
    @JoinColumn(name = "board_idx")
    private ChannelBoard channelBoard;

    @OneToMany(mappedBy = "boardComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Likes> likesList;

    @Builder
    public BoardComment(String content, ChannelBoard channelBoard) {
        this.content = content;
        this.channelBoard = channelBoard;
        this.isDeleted = false;
        this.likesCount = 0;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void decrementLikeCount() {
        this.likesCount = this.likesCount - 1;
    }

    public void incrementLikeCount() {
        this.likesCount = this.likesCount + 1;
    }
}