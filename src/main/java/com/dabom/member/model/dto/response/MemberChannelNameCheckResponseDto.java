package com.dabom.member.model.dto.response;

public record MemberChannelNameCheckResponseDto(Boolean isDuplicate) {
    public static MemberChannelNameCheckResponseDto of(Boolean isDuplicate) {
        return new MemberChannelNameCheckResponseDto(isDuplicate);
    }
}
