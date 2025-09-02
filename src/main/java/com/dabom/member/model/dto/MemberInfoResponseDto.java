package com.dabom.member.model.dto;

import com.dabom.member.model.entity.Member;

public record MemberInfoResponseDto(Integer id, String name, String content, String email,Integer videoCount) {
    public static MemberInfoResponseDto toDto(Member member) {
        return new MemberInfoResponseDto(member.getIdx(), member.getName(), member.getContent(), member.getEmail(),
                member.getVideoList().size());
    }
}
