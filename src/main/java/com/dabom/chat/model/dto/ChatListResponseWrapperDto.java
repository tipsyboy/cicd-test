package com.dabom.chat.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChatListResponseWrapperDto {
    private Integer currentMemberIdx;
    private List<ChatRoomListResponseDto> chatRooms;
}
