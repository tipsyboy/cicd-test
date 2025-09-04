package com.dabom.chat.model.dto;

import com.dabom.chat.model.entity.Chat;
import com.dabom.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString // Add this line
public class ChatMessageDto extends BaseEntity {
    private Long roomIdx;
    private Integer senderIdx;
    private String senderName;
    private Integer recipientIdx;
    
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;


    public static ChatMessageDto fromEntity(Chat entity) {
        return ChatMessageDto.builder()
                .roomIdx(entity.getRoom().getIdx())
                .senderIdx(entity.getSender().getIdx())
                .senderName(entity.getSender().getName())
                .recipientIdx(entity.getRecipient().getIdx())
                
                .message(entity.getMessage())
                .isRead(entity.getIsRead())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
