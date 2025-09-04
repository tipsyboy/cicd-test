package com.dabom.member.model.entity;

import com.dabom.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(indexes = {
        @Index(name = "idx_channel", columnList = "channel_idx"),
        @Index(name = "idx_subscriber", columnList = "subscriber_idx")
})
@Entity
@Getter
@NoArgsConstructor
public class Subscribe extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @ManyToOne
    @JoinColumn(name = "channel_idx")
    private Member channel;

    @ManyToOne
    @JoinColumn(name = "subscriber_idx")
    private Member subscriber;
    private Boolean voteScore;
    private Boolean isDelete;

    @Builder
    public Subscribe(Member channel, Member subscriber) {
        this.channel = channel;
        this.subscriber = subscriber;
        this.voteScore = false;
        this.isDelete = false;
    }

    public void voteScore() {
        this.voteScore = true;
    }

    public void cancelScore() {
        this.voteScore = false;
    }

    public void delete() {
        this.isDelete = true;
    }

    public void rollBack() {
        this.isDelete = false;
    }
}
