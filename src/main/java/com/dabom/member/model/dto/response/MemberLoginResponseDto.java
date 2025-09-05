package com.dabom.member.model.dto.response;

public record MemberLoginResponseDto(String jwt, String refreshJwt, String channelName) {
    public static MemberLoginResponseDto of(String jwt, String refreshJwt, String channelName){
        return new MemberLoginResponseDto(jwt, refreshJwt, channelName);
    }
}
