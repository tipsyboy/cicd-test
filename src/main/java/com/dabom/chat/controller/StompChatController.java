package com.dabom.chat.controller;

import com.dabom.chat.model.dto.ChatMessageDto;
import com.dabom.chat.serivce.ChatService;
import com.dabom.member.security.dto.MemberDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final SimpMessagingTemplate template;
    private final ChatService chatService;

    @MessageMapping(value = "/chat/send")
    public void message(ChatMessageDto message, @AuthenticationPrincipal MemberDetailsDto userDetails) {

        ChatMessageDto savedMessage = chatService.sendMessage(message, userDetails);

        // 받는 사람에게 메시지 전송
        template.convertAndSendToUser(
                savedMessage.getRecipientName(),
                "/queue/messages",
                savedMessage
        );

        // 보낸 사람에게도 메시지 전송 (UI 동기화)
        template.convertAndSendToUser(
                userDetails.getUsername(),
                "/queue/messages",
                savedMessage
        );
    }
}
