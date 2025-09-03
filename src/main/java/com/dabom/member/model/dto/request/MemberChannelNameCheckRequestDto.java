package com.dabom.member.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberChannelNameCheckRequestDto {
    @NotBlank
    private String channelName;
}
