package com.dabom.member.model.dto.response;

import com.dabom.member.model.entity.Member;

import java.util.regex.Pattern;

public record MemberInfoResponseDto(Integer id, String name, String content, String email, Integer videoCount) {

    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public static MemberInfoResponseDto toDto(Member member) {
        String processedEmail = processEmail(member.getEmail());

        return new MemberInfoResponseDto(
                member.getIdx(),
                member.getName(),
                member.getContent(),
                processedEmail,
                member.getVideoList().size());
    }

    private static String processEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "비공개";
        }

        // 이메일 형식이면 그대로 반환
        if (pattern.matcher(email).matches()) {
            return email;
        }

        // 이메일 형식이 아니면 (OAuth2 ID값) 커스텀 값으로 대체
        return "소셜로그인 사용자";
    }
}
