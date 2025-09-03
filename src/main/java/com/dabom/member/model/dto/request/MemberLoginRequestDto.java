package com.dabom.member.model.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class MemberLoginRequestDto {
    @Pattern(message = "이메일 형식을 사용해주세요", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;
//    @Pattern(message = "비밀번호는 숫자,영문 대소문자,특수문자( !@#$%^&*() )를 조합해 8~20자로 생성해주세요.", regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*()])(?=.*[0-9]).{8,20}$")
    private String password;
}
