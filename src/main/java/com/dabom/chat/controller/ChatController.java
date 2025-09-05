package com.dabom.chat.controller;

import com.dabom.chat.constansts.SwaggerConstants;
import com.dabom.chat.model.dto.ChatMessageDto;
import com.dabom.chat.model.dto.ChatRoomListResponseDto;
import com.dabom.chat.model.dto.ChatRoomReadResponseDto;
import com.dabom.chat.model.dto.ChatRoomRegisterRequestDto;
import com.dabom.chat.serivce.ChatService;
import com.dabom.common.BaseResponse;
import com.dabom.common.SliceBaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.chat.model.dto.ChatListResponseWrapperDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "채팅 기능", description = "채팅 관련 API")
@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @Operation(summary = "채팅방 생성", description = "특정 비디오에 대한 채팅방을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "채팅방 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = SwaggerConstants.CREATE_CHAT_ROOM_RESPONSE)))
    })
    @PostMapping("/room/{videoIdx}")
    public ResponseEntity<BaseResponse<Long>> createRoom(
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto, @PathVariable Integer videoIdx) {
        Integer member1Idx = memberDetailsDto.getIdx();
        long chatRoomId = chatService.createRoom(member1Idx, videoIdx);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.of(chatRoomId, HttpStatus.CREATED, "chatroomId"));
    }

    @Operation(summary = "채팅방 목록 조회", description = "사용자가 참여하고 있는 채팅방 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = SwaggerConstants.FIND_CHAT_ROOM_RESPONSE)))
    })
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

    @Operation(summary = "채팅 내용 조회", description = "특정 채팅방의 메시지 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = SwaggerConstants.GET_MESSAGES_RESPONSE)))
    })
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
