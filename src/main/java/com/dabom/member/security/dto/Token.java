package com.dabom.member.security.dto;

public record Token(String aT, String rT) {
    public static Token toDto(String aT, String rT) {
        return new Token(aT, rT);
    }
}
