package com.dabom.together.model.dto.response;

import com.dabom.member.model.entity.Member;
import com.dabom.together.model.entity.Together;
import com.dabom.together.model.entity.TogetherJoinMember;

import java.util.List;

public record TogetherMemberListResponseDto(List<TogetherMemberInfoResponseDto> members) {
    public static TogetherMemberListResponseDto toDto(List<TogetherJoinMember> memberlist, Member master) {
        List<TogetherMemberInfoResponseDto> list =
                memberlist.stream()
                        .filter(member -> !member.getIsDelete())
                        .map(member -> TogetherMemberInfoResponseDto.toDto(member, master))
                        .toList();

        return new TogetherMemberListResponseDto(list);
    }
}
