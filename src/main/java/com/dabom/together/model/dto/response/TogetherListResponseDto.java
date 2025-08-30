package com.dabom.together.model.dto.response;

import com.dabom.together.model.entity.Together;

import java.util.List;

public record TogetherListResponseDto(List<TogetherInfoResponseDto> togethers) {
    public static TogetherListResponseDto toDto(List<Together> togethers) {
        List<TogetherInfoResponseDto> togetherList = togethers.stream().map(TogetherInfoResponseDto::toDto).toList();

        return new TogetherListResponseDto(togetherList);
    }

    public static TogetherListResponseDto of(List<TogetherInfoResponseDto> togethers) {
        return new TogetherListResponseDto(togethers);
    }
}
