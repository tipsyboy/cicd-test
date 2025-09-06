package com.dabom.member.service;

import com.dabom.member.exception.MemberException;
import com.dabom.member.model.dto.request.SubscribeCreateDto;
import com.dabom.member.model.dto.response.MemberInfoResponseDto;
import com.dabom.member.model.entity.Member;
import com.dabom.member.model.entity.Subscribe;
import com.dabom.member.repository.MemberRepository;
import com.dabom.member.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dabom.member.exception.MemberExceptionType.MEMBER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubscribeService {
    private final SubscribeRepository subscribeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Integer create(SubscribeCreateDto dto, Integer subscriberIdx, Integer channelIdx){
        Member subscriber = memberRepository.findById(subscriberIdx)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        Member channel = memberRepository.findById(channelIdx)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        if (subscribeRepository.findBySubscriberAndChannel(subscriber, channel).isPresent()) {
            return -1;
        }

        Subscribe subscribe = dto.toEntity(channel, subscriber);
        subscribeRepository.save(subscribe);

        return subscribe.getIdx();
    }

    public List<MemberInfoResponseDto> sublist(Integer subscriberIdx) {
        Member subscriber = memberRepository.findById(subscriberIdx)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        List<Subscribe> subscriptions = subscriber.getSubscribes();

        return subscriptions.stream().map(Subscribe::getChannel)
                .map(MemberInfoResponseDto::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void unsubscribe(Integer subscriberIdx, Integer channelIdx) {
        Member subscriber = memberRepository.findById(subscriberIdx)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Member channel = memberRepository.findById(channelIdx)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        Optional<Subscribe> optionalSubscribe = subscribeRepository.findBySubscriberAndChannel(subscriber, channel);

        if (optionalSubscribe.isPresent()) {
            subscribeRepository.delete(optionalSubscribe.get());
        } else {
            throw new MemberException(MEMBER_NOT_FOUND);
        }
    }

    public Boolean isSubscribe(Integer subscriberIdx, Integer channelIdx) {
        Member subscriber = memberRepository.findById(subscriberIdx)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Member channel = memberRepository.findById(channelIdx)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Optional<Subscribe> optionalSubscribe = subscribeRepository.findBySubscriberAndChannel(subscriber, channel);

        return optionalSubscribe.isPresent();
    }
}
