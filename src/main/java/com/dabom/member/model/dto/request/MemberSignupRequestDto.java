package com.dabom.member.model.dto.request;

import com.dabom.member.model.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberSignupRequestDto {
    @Pattern(message = "이메일 형식을 사용해주세요", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;
    @NotBlank
    private String channelName;
//    @Pattern(message = "비밀번호는 숫자,영문 대소문자,특수문자( !@#$%^&*() )를 조합해 8~20자로 생성해주세요.", regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*()])(?=.*[0-9]).{8,20}$")
    private String password;

    private String memberRole;

    @Builder
    public MemberSignupRequestDto(String email, String channelName, String password, String memberRole) {
        this.email = email;
        this.channelName = channelName;
        this.password = password;
        this.memberRole = memberRole;
    }

    public Member toEntity(String encodePassword) {
        return Member.builder()
                .email(email)
                .name(channelName)
                .password(encodePassword)
                .memberRole(memberRole)
                .build();
    }
}
