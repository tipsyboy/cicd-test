package com.dabom.member.model.dto.request;

import com.dabom.member.model.entity.Member;
import com.dabom.member.model.entity.Subscribe;
import lombok.Builder;

public class SubscribeCreateDto {
    private Integer channelIdx;
    private Integer subscriberIdx;
    private Boolean isdelete;


    @Builder
    public Subscribe toEntity(Member channel, Member subscriber) {
        return Subscribe.builder()
                .channel(channel)
                .subscriber(subscriber)
                .build();
    }
}
