package com.dabom.together.model.entity;

import com.dabom.member.model.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TogetherJoinMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @ManyToOne
    @JoinColumn(name = "member_idx")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "together_idx")
    private Together together;
    private Boolean isJoin;
    private Boolean isDelete;

    @Builder
    public TogetherJoinMember(Member member, Together together, Boolean isJoin, Boolean isDelete) {
        this.member = member;
        this.together = together;
        this.isJoin = isJoin;
        this.isDelete = isDelete;
    }

    public void join() {
        this.isJoin = true;
    }

    public void leave() {
        this.isJoin = false;
    }

    public void comeBackTogether() {
        this.isDelete = false;
    }

    public void leaveTogether() {
        this.isDelete = true;
    }

    public void expel() {
        this.isDelete = true;
    }
}
