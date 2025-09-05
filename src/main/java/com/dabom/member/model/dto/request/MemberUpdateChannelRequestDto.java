package com.dabom.member.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberUpdateChannelRequestDto {
    private String name;
    private String content;
    private Integer profileImageIdx;
    private Integer bannerImageIdx;
}