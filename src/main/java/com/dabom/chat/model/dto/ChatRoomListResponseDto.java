package com.dabom.chat.model.dto;

import com.dabom.chat.model.entity.Chat;
import com.dabom.chat.model.entity.ChatRoom;
import com.dabom.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomListResponseDto extends BaseEntity {
    private Long idx;
    private Integer member1Idx;
    private String member1Name;
    private Integer member2Idx;
    private String member2Name;
    private String opponentProfileImg; // 상대방 프로필 이미지
    private String lastMessage;
    private Boolean isLastMessageRead;
    private LocalDateTime lastMessageTime;
    private long unreadCount; // 안 읽은 메시지 수
    private Boolean isDeleted;

    public static ChatRoomListResponseDto fromEntity(ChatRoom chatRoom, Chat lastChat, long unreadCount, String opponentProfileImg) {
        return ChatRoomListResponseDto.builder()
                .idx(chatRoom.getIdx())
                .member1Idx(chatRoom.getMember1().getIdx())
                .member1Name(chatRoom.getMember1().getName())
                .member2Idx(chatRoom.getMember2().getIdx())
                .member2Name(chatRoom.getMember2().getName())
                .opponentProfileImg(opponentProfileImg)
                .lastMessage(lastChat != null ? lastChat.getMessage() : null)
                .isLastMessageRead(lastChat != null ? lastChat.getIsRead() : null)
                .lastMessageTime(lastChat != null ? lastChat.getCreatedAt() : null)
                .unreadCount(unreadCount)
                .isDeleted(chatRoom.getIsDeleted())
                .build();
    }

}
