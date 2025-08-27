package com.dabom.video.model;

import lombok.Getter;

@Getter
public enum VideoTag {
    NONE("없음"),
    ENTERTAINMENT("엔터테인먼트"),
    EDUCATION("교육"),
    GAMING("게임"),
    LIFESTYLE("라이프스타일"),
    MUSIC("음악"),
    SPORTS("스포츠"),
    NEWS("뉴스·시사"),
    TECH("과학·기술"),
    TRAVEL("여행"),
    REVIEW("리뷰·쇼핑"),
    ETC("기타");

    private final String displayName;

    VideoTag(String displayName) {
        this.displayName = displayName;
    }
}