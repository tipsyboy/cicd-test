package com.dabom.member.model.dto.response;

public record SubscribeInfoResponseDto(Boolean isSubscribe) {
    public static SubscribeInfoResponseDto toDto(Boolean isSubscribe) {
        return new SubscribeInfoResponseDto(isSubscribe);
    }
}
