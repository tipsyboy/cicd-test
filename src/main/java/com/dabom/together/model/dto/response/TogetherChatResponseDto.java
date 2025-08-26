package com.dabom.together.model.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record TogetherChatResponseDto(String name, String message, Boolean isJoin, Integer users, String now, Integer userIdx) {
    public static TogetherChatResponseDto toDtoBySend(String name, String message, Integer users, Integer userIdx) {
        LocalDateTime now = LocalDateTime.now();

        // 포맷 지정 (예: 2025-08-25 14:55:30)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH시 mm분 ss초");
        String formattedNow = now.format(formatter);
        return new TogetherChatResponseDto(name, message, false, users, formattedNow, userIdx);
    }

    public static TogetherChatResponseDto toDtoByJoin(String name, Integer users) {
        LocalDateTime now = LocalDateTime.now();

        // 포맷 지정 (예: 2025-08-25 14:55:30)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH시 mm분 ss초");
        String formattedNow = now.format(formatter);
        return new TogetherChatResponseDto(name, null, true, users, formattedNow, null);
    }
}
