package com.dabom.member.service;

import com.dabom.member.exception.MemberException;
import com.dabom.member.exception.SubscribeException;
import com.dabom.member.model.entity.Member;
import com.dabom.member.model.entity.Subscribe;
import com.dabom.member.repository.MemberRepository;
import com.dabom.member.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.dabom.member.exception.MemberExceptionType.MEMBER_NOT_FOUND;
import static com.dabom.member.exception.SubscribeExceptionType.NOTFOUND_SUBSCRIBE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubscribeService {
    private final MemberRepository memberRepository;
    private final SubscribeRepository subscribeRepository;

    @Transactional
    public void subscribe(Integer memberId, Integer channelId) {
        Member subscriber = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Member channel = memberRepository.findById(channelId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Optional<Subscribe> optional = subscribeRepository.findByChannelAndSubscriber(channel, subscriber);

        if(optional.isPresent()) {
            Subscribe subscribe = optional.get();
            if(subscribe.getIsDelete()) {
                subscribe.rollBack();
                subscribeRepository.save(subscribe);
            }
            return;
        }

        Subscribe subscribe = Subscribe.builder()
                .channel(channel)
                .subscriber(subscriber)
                .build();
        channel.countSubscribe();

        memberRepository.save(channel);
        subscribeRepository.save(subscribe);
    }

    @Transactional
    public void subscribeDelete(Integer memberId, Integer channelId) {
        Member subscriber = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Member channel = memberRepository.findById(channelId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Optional<Subscribe> optional = subscribeRepository.findByChannelAndSubscriber(channel, subscriber);
        if(optional.isPresent()) {
            Subscribe subscribe = optional.get();
            if(!subscribe.getIsDelete()) {
                subscribe.delete();
                channel.cancelSubscribe();
                memberRepository.save(channel);
                subscribeRepository.save(subscribe);
            }
            return;
        }
        throw new SubscribeException(NOTFOUND_SUBSCRIBE);
    }

    public Boolean getSubscribe(Integer memberId, Integer channelId) {
        Member subscriber = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Member channel = memberRepository.findById(channelId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Optional<Subscribe> optional = subscribeRepository.findByChannelAndSubscriber(channel, subscriber);
        if(optional.isPresent() && !optional.get().getIsDelete()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
