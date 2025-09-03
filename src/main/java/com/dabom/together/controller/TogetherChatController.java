package com.dabom.together.controller;

import com.dabom.member.model.dto.response.MemberInfoResponseDto;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.member.service.MemberService;
import com.dabom.together.model.dto.request.TogetherMoveVideoRequestDto;
import com.dabom.together.model.dto.response.TogetherChatResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public class TogetherChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MemberService memberService;
    private final Map<String, Set<Integer>> topicSessions = new ConcurrentHashMap<>();

    @MessageMapping("/together/{togetherId}")
    public void sendMessage(Principal principal,
            @DestinationVariable Integer togetherId, @Payload String message) {
        Authentication authentication = (Authentication)principal;
        MemberDetailsDto memberDetailsDto = (MemberDetailsDto)authentication.getPrincipal();

        MemberInfoResponseDto memberInfo = memberService.getMemberInfo(memberDetailsDto);
        String destination = "/topic/together/" + togetherId; // 토픽 키
        int userCount = topicSessions.getOrDefault(destination, Collections.emptySet()).size();

        TogetherChatResponseDto res = TogetherChatResponseDto.toDtoBySend(memberInfo.name(), message, userCount, memberInfo.id());

        messagingTemplate.convertAndSend("/topic/together/" + togetherId, res);
    }

    @MessageMapping("/master/together/{togetherId}")
    public void moveVideos(Principal principal,
                            @DestinationVariable Integer togetherId, @Payload TogetherMoveVideoRequestDto dto) {
        System.out.println(dto.getCurrentTime());
        messagingTemplate.convertAndSend("/topic/master/together/" + togetherId, dto);
    }

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        Principal userPrincipal = headerAccessor.getUser();
        String destination = headerAccessor.getDestination();

        if (destination != null && destination.startsWith("/topic/together/")) {
            // 환영 메시지 생성
            Authentication authentication = (Authentication) userPrincipal;
            MemberDetailsDto memberDetailsDto = (MemberDetailsDto) authentication.getPrincipal();
            MemberInfoResponseDto memberInfo = memberService.getMemberInfo(memberDetailsDto);

            // 토픽별 접속자 세션 관리
            topicSessions.computeIfAbsent(destination, k -> ConcurrentHashMap.newKeySet()).add(memberInfo.id());

            Integer userCount = topicSessions.get(destination).size();

            TogetherChatResponseDto welcome = TogetherChatResponseDto.toDtoByJoin(memberInfo.name(), userCount);
            messagingTemplate.convertAndSend(destination, welcome);
        }
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        Principal userPrincipal = headerAccessor.getUser();

        if (userPrincipal == null) {
            return; // 인증정보 없으면 종료
        }

        Authentication authentication = (Authentication) userPrincipal;
        MemberDetailsDto memberDetailsDto = (MemberDetailsDto) authentication.getPrincipal();
        Integer memberId = memberDetailsDto.getIdx();

        // 연결된 모든 토픽에 대해 해당 멤버 아이디 제거
        topicSessions.  forEach((topic, memberSet) -> {
            boolean removed = memberSet.remove(memberId);
            if (removed) {
                messagingTemplate.convertAndSend(topic, "");
            }
        });
    }
}
