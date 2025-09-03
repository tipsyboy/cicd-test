package com.dabom.member.utils;

import com.dabom.member.model.dto.request.MemberSignupRequestDto;
import com.dabom.member.service.MemberService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {
    private final MemberService memberService;

    @PostConstruct
    public void initData() {
        MemberSignupRequestDto dto1 = MemberSignupRequestDto.builder()
                .email("DabomTotalManager@dabom.com")
                .channelName("DabomTopManager")
                .password("Dabom!234")
                .memberRole("MANAGER")
                .build();

        MemberSignupRequestDto dto2 = MemberSignupRequestDto.builder()
                .email("DabomSubManager@dabom.com")
                .channelName("DabomSubManager")
                .password("Dabom1@34")
                .memberRole("MANAGER")
                .build();

        if(!memberService.checkMemberEmail("DabomTotalManager@dabom.com").isDuplicate()) {
            memberService.signUpMember(dto1);
        } if(!memberService.checkMemberEmail("DabomSubManager@dabom.com").isDuplicate()) {
            memberService.signUpMember(dto2);
        }
    }
}
