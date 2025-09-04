package com.dabom.chat.controller;

import com.dabom.chat.model.dto.ChatMessageDto;
import com.dabom.chat.serivce.ChatService;
import com.dabom.member.security.dto.MemberDetailsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final SimpMessagingTemplate template;
    private final ChatService chatService;

    @MessageMapping(value = "/chat/send")
    public void message(@Payload ChatMessageDto message,
                        Principal principal) {

        log.info("Received message payload: {}", message);
        log.info("Full ChatMessageDto received from frontend: {}", message.toString());

        Authentication authentication = (Authentication) principal;
        MemberDetailsDto memberDetailsDto = (MemberDetailsDto) authentication.getPrincipal();

        ChatMessageDto savedMessage = chatService.sendMessage(message, memberDetailsDto);

        template.convertAndSendToUser(
                savedMessage.getRecipientIdx().toString(),
                "/queue/messages",
                savedMessage
        );

        // 보낸 사람에게도 메시지 전송 (UI 동기화)
        template.convertAndSendToUser(
                memberDetailsDto.getIdx().toString(),
                "/queue/messages",
                savedMessage
        );
    }
}
