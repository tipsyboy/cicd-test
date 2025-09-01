package com.dabom.chat.controller;

import com.dabom.chat.model.dto.ChatMessageDto;
import com.dabom.chat.model.dto.ChatRoomListResponseDto;
import com.dabom.chat.model.dto.ChatRoomReadResponseDto;
import com.dabom.chat.model.dto.ChatRoomRegisterRequestDto;
import com.dabom.chat.serivce.ChatService;
import com.dabom.common.BaseResponse;
import com.dabom.common.SliceBaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.chat.model.dto.ChatListResponseWrapperDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/room")
    public ResponseEntity<BaseResponse<Long>> createRoom(@RequestBody ChatRoomRegisterRequestDto dto) {
        long result = chatService.createRoom(dto);
        return ResponseEntity.ok(BaseResponse.of(result, HttpStatus.OK));
    }

    @GetMapping("/list")
    public ResponseEntity<BaseResponse<ChatListResponseWrapperDto>> list( @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        Integer memberidx = memberDetailsDto.getIdx();
        System.out.println(memberidx);
        List<ChatRoomListResponseDto> chatRooms = chatService.getList(memberidx);

        ChatListResponseWrapperDto response = ChatListResponseWrapperDto.builder()
                .currentMemberIdx(memberidx)
                .chatRooms(chatRooms)
                .build();

        return ResponseEntity.ok(BaseResponse.of(response, HttpStatus.OK));
    }

    @GetMapping("/read/{room_idx}")
    public ResponseEntity<BaseResponse<SliceBaseResponse<ChatRoomReadResponseDto>>> readRoom(
            @PathVariable("room_idx") Long roomIdx,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Principal principal) {
        Integer memberIdx = chatService.getMember(principal);
        System.out.println(memberIdx);
        SliceBaseResponse<ChatRoomReadResponseDto> result = chatService.readRoom(roomIdx, memberIdx, page, size);
        return ResponseEntity.ok(BaseResponse.of(result, HttpStatus.OK));
    }

}
