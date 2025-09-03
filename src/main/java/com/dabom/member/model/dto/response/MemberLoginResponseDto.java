package com.dabom.member.model.dto.response;

public record MemberLoginResponseDto(String jwt, String channelName) {
    public static MemberLoginResponseDto of(String jwt, String channelName){
        return new MemberLoginResponseDto(jwt, channelName);
    }
}
