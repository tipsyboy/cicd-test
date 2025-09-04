package com.dabom.chat.model.dto;

import com.dabom.chat.model.entity.ChatRoom;
import com.dabom.member.model.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class ChatRoomRegisterRequestDto {

    private Member member1;
    private Member member2;

    @Builder
    public ChatRoomRegisterRequestDto(Member member1, Member member2) {
        this.member1 = member1;
        this.member2 = member2;
    }

    public ChatRoom toEntity(){
        return ChatRoom.builder()
                .member1(this.member1)
                .member2(this.member2)
                .build();
    }
}
