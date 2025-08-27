package com.dabom.channelboard.model.entity;

import com.dabom.boardcomment.model.entity.BoardComment;
import com.dabom.common.BaseEntity;
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
    private Integer likesCount;
    @Setter
    private Boolean isDeleted;


    @Builder
    public ChannelBoard(Integer idx, String title, String contents, Member channel) {
        this.idx = idx;
        this.title = title;
        this.contents = contents;
        this.isDeleted = false;
        this.channel = channel;
        this.likesCount = 0;
    }

    @OneToMany(mappedBy = "channelBoard")
    private List<BoardComment> boardCommentList;

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
