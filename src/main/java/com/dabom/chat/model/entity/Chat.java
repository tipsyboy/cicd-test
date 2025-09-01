package com.dabom.chat.model.entity;

import com.dabom.chat.model.dto.ChatMessageDto;
import com.dabom.common.BaseEntity;
import com.dabom.member.model.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @ManyToOne
    @JoinColumn(name = "room_idx", nullable = false)
    private ChatRoom room;

    @ManyToOne
    @JoinColumn(name = "sender_idx", nullable = false)
    private Member sender;

    @ManyToOne
    @JoinColumn(name = "recipient_idx", nullable = false)
    private Member recipient;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @Column(nullable = false)
    private Boolean isRead = false;

    @Builder
    public Chat(String message, ChatRoom room, Member sender, Member recipient) {
        this.message = message;
        this.room = room;
        this.sender = sender;
        this.recipient = recipient;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public void deleteMessage() {
        this.isDeleted = true;
    }

    public static Chat from(ChatMessageDto messageDto, ChatRoom room) {
        Member sender = room.getMember1().getIdx().equals(messageDto.getSenderIdx()) ? room.getMember1() : room.getMember2();
        Member recipient = room.getMember1().getIdx().equals(messageDto.getRecipientIdx()) ? room.getMember1() : room.getMember2();
        return Chat.builder()
                .message(messageDto.getMessage())
                .room(room)
                .sender(sender)
                .recipient(recipient)
                .build();
    }
}