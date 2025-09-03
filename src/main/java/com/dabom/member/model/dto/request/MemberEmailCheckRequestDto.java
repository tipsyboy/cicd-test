package com.dabom.member.model.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class MemberEmailCheckRequestDto {
    @Pattern(message = "이메일 형식을 사용해주세요", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;
}
