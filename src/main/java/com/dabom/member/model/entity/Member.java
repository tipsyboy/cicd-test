package com.dabom.member.model.entity;

import com.dabom.channelboard.model.entity.ChannelBoard;
import com.dabom.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(unique = true)
    private String email;
    private String name;
    private String password;
    private String content;
    @Enumerated(EnumType.STRING)
    @Column(name = "member_role")
    private MemberRole memberRole;

    // 내가 구독한 사람
    @OneToMany(mappedBy = "subscriber", fetch = FetchType.LAZY)
    private List<Subscribe> subscribes;
    // 나를 구독한 사람
    @OneToMany(mappedBy = "channel", fetch = FetchType.LAZY)
    private List<Subscribe> subscriptions;
    // 채널에 올라가는 게시글 모음
    @OneToMany(mappedBy = "channel", fetch = FetchType.LAZY)
    private List<ChannelBoard> channelBoards;

    private Long sumScore;
    private Long sumScoreMember;
//    private ImageFile profileImage;
    private Boolean isDeleted;

    @Builder
    public Member(String email, String name, String password, String memberRole) {
        this.email = email;
        this.name = name;
        this.content = null;
        this.password = password;
        this.memberRole = MemberRole.valueOf(memberRole);
        this.sumScore = 0L;
        this.sumScoreMember = 0L;
        this.isDeleted = false;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void voidScore(Long score) {
        this.sumScore += score;
        this.sumScoreMember++;
    }

    public void deleteMember() {
        this.isDeleted = true;
    }
}
