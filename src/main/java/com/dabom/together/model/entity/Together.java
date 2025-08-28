package com.dabom.together.model.entity;

import com.dabom.member.model.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Together {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;
    private String title;
    private String videoUrl;
    private Integer maxMemberNum;
    private Integer joinMemberNum;
    private Boolean isOpen;
    private Boolean isDelete;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36) DEFAULT (UUID())", updatable = false, insertable = false)
    private UUID code;

    @ManyToOne
    @JoinColumn(name = "master_idx")
    private Member master;

    @OneToMany(mappedBy = "together")
    private List<TogetherJoinMember> members;

    @Builder
    public Together(String title, String videoUrl, Integer maxMemberNum, Boolean isOpen, Boolean isDelete, Member master) {
        this.title = title;
        this.videoUrl = videoUrl;
        this.maxMemberNum = maxMemberNum;
        this.isOpen = isOpen;
        this.isDelete = isDelete;
        this.master = master;
        this.joinMemberNum = 1;
    }

    public void joinMember() {
        this.joinMemberNum++;
    }

    public void leaveMember() {
        this.joinMemberNum--;
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeTogetherIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public void changeMaxMemberNumber(int maxNumber) {
        this.maxMemberNum = maxNumber;
    }

    public void changeVideo(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void deleteTogether() {
        this.isDelete = true;
    }
}
