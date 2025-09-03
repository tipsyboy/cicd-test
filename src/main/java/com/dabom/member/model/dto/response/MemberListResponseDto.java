package com.dabom.member.model.dto.response;

import com.dabom.member.model.entity.Member;

import java.util.List;

public record MemberListResponseDto(List<MemberInfoResponseDto> members) {
    public static MemberListResponseDto toDto(List<Member> memberList) {
        return new MemberListResponseDto(memberList.stream().map(MemberInfoResponseDto::toDto).toList());
    }
}
