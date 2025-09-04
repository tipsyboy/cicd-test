package com.dabom.member.repository;

import com.dabom.member.model.entity.Member;
import com.dabom.member.model.entity.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<Subscribe, Integer> {
    Optional<Subscribe> findByChannelAndSubscriber(Member channel, Member Subscriber);
}
