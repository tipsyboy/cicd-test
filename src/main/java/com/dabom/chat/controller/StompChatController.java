package com.dabom.chat.controller;

import com.dabom.chat.model.dto.ChatMessageDto;
import com.dabom.chat.serivce.ChatService;
import com.dabom.member.security.dto.MemberDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final SimpMessagingTemplate template;
    private final ChatService chatService;

    @MessageMapping(value = "/chat/send")
    public void message(@Payload ChatMessageDto message,
                        Principal principal) {

        Authentication authentication = (Authentication) principal;
        MemberDetailsDto memberDetailsDto = (MemberDetailsDto) authentication.getPrincipal();

        ChatMessageDto savedMessage = chatService.sendMessage(message, memberDetailsDto);

        template.convertAndSendToUser(
                savedMessage.getRecipientName(),
                "/queue/messages",
                savedMessage
        );

        // 보낸 사람에게도 메시지 전송 (UI 동기화)
        template.convertAndSendToUser(
                memberDetailsDto.getUsername(),
                "/queue/messages",
                savedMessage
        );
    }
}
