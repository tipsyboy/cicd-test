package com.dabom.together.controller;

import com.dabom.member.model.dto.response.MemberInfoResponseDto;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.member.service.MemberService;
import com.dabom.together.model.dto.request.TogetherMasterControlRequestDto;
import com.dabom.together.model.dto.request.TogetherMoveVideoRequestDto;
import com.dabom.together.model.dto.response.TogetherChatResponseDto;
import com.dabom.together.service.TogetherService;
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
    private final TogetherService togetherService;
    private final Map<String, Set<Integer>> topicSessions = new ConcurrentHashMap<>();

    @MessageMapping("/together/{togetherIdx}")
    public void sendMessage(Principal principal,
                            @DestinationVariable Integer togetherIdx, @Payload String message) {
        MemberDetailsDto memberDetailsDto = getMemberDetailsDto((Authentication) principal);

        MemberInfoResponseDto memberInfo = memberService.getMemberInfo(memberDetailsDto);
        String destination = "/topic/together/" + togetherIdx; // 토픽 키
        int userCount = topicSessions.getOrDefault(destination, Collections.emptySet()).size();

        TogetherChatResponseDto res = TogetherChatResponseDto.toDtoBySend(memberInfo.name(), message, userCount, memberInfo.id());

        messagingTemplate.convertAndSend("/topic/together/" + togetherIdx, res);
    }

    @MessageMapping("/master/together/{togetherIdx}")
    public void moveVideos(Principal principal,
                           @DestinationVariable Integer togetherIdx, @Payload TogetherMoveVideoRequestDto dto) {
        MemberDetailsDto memberDetailsDto = getMemberDetailsDto((Authentication) principal);
        togetherService.isMaster(togetherIdx, memberDetailsDto);
        messagingTemplate.convertAndSend("/topic/master/together/" + togetherIdx, dto);
    }

    @MessageMapping("/master/control/together/{togetherIdx}")
    public void masterControl(Principal principal,
                              @DestinationVariable Integer togetherIdx, @Payload TogetherMasterControlRequestDto dto) {
        MemberDetailsDto memberDetailsDto = getMemberDetailsDto((Authentication) principal);
        togetherService.isMaster(togetherIdx, memberDetailsDto);
        messagingTemplate.convertAndSend("/topic/master/control/together/" + togetherIdx, dto);
    }

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Principal userPrincipal = headerAccessor.getUser();
        String destination = headerAccessor.getDestination();

        if (destination != null && destination.startsWith("/topic/together/")) {
            // 환영 메시지 생성
            MemberDetailsDto memberDetailsDto = getMemberDetailsDto((Authentication) userPrincipal);
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
        Principal userPrincipal = headerAccessor.getUser();

        if (userPrincipal == null) {
            return; // 인증정보 없으면 종료
        }

        MemberDetailsDto memberDetailsDto = getMemberDetailsDto((Authentication) userPrincipal);
        Integer memberId = memberDetailsDto.getIdx();

        // 연결된 모든 토픽에 대해 해당 멤버 아이디 제거
        topicSessions.  forEach((topic, memberSet) -> {
            boolean removed = memberSet.remove(memberId);
            if (removed) {
                messagingTemplate.convertAndSend(topic, "");
            }
        });
    }

    private MemberDetailsDto getMemberDetailsDto(Authentication principal) {
        Authentication authentication = principal;
        MemberDetailsDto memberDetailsDto = (MemberDetailsDto)authentication.getPrincipal();
        return memberDetailsDto;
    }
}
