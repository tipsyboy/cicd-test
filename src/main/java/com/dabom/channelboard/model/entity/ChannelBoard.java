package com.dabom.channelboard.model.entity;

import com.dabom.boardcomment.model.entity.BoardComment;
import com.dabom.common.BaseEntity;
import com.dabom.likes.model.likes.Likes;
import com.dabom.member.model.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.net.Inet4Address;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChannelBoard extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;
    private String title;
    private String contents;

    @Column(name = "likes_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer likesCount = 0;

    @Setter
    private Boolean isDeleted;


    @Builder
    public ChannelBoard(Integer idx, String title, String contents, Member channel) {
        this.idx = idx;
        this.title = title;
        this.contents = contents;
        this.isDeleted = false;
        this.channel = channel;
    }

    @OneToMany(mappedBy = "channelBoard")
    private List<BoardComment> boardCommentList;

    @OneToMany(mappedBy = "channelBoard")
    private List<Likes> likesList;

    @ManyToOne
    @JoinColumn(name = "member_idx")
    private Member channel;

    public void update(String title,String contents) {
        this.title = title;
        this.contents = contents;
    }

    public void decrementLikeCount() {
        this.likesCount = this.likesCount - 1;
    }

    public void incrementLikeCount() {
        this.likesCount = this.likesCount + 1;
    }

}
