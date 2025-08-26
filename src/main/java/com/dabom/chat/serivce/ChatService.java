package com.dabom.chat.serivce;

import com.dabom.chat.model.dto.ChatMessageDto;
import com.dabom.chat.model.dto.ChatRoomListResponseDto;
import com.dabom.chat.model.dto.ChatRoomReadResponseDto;
import com.dabom.chat.model.dto.ChatRoomRegisterRequestDto;
import com.dabom.chat.model.entity.Chat;
import com.dabom.chat.model.entity.ChatRoom;
import com.dabom.chat.repository.ChatRepository;
import com.dabom.chat.repository.ChatRoomRepository;
import com.dabom.common.SliceBaseResponse;
import com.dabom.member.repository.MemberRepository;
import com.dabom.member.security.dto.MemberDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;

    public long createRoom(ChatRoomRegisterRequestDto dto) {
        ChatRoom result = chatRoomRepository.save(dto.toEntity());
        return result.getIdx();
    }

    public Integer getMember(Principal principal) {
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) principal;
        MemberDetailsDto memberDetails = (MemberDetailsDto) authToken.getPrincipal();
        return memberDetails.getIdx();

    }

    public List<ChatRoomListResponseDto> getList(Integer memberIdx) {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByMemberIdxAndIsDeleted(memberIdx);

        return chatRooms.stream()
                .map(chatRoom -> {
                    Optional<Chat> lastChat = chatRepository.findTopByRoomIdxAndIsDeletedOrderByCreatedAtDesc(chatRoom.getIdx());
                    return ChatRoomListResponseDto.fromEntity(chatRoom, lastChat.orElse(null));
                })
                .collect(Collectors.toList());
    }

    public SliceBaseResponse<ChatRoomReadResponseDto> readRoom(Long roomIdx, Integer memberIdx, int page, int size) {
        // 채팅방 존재 여부 및 권한 확인
        ChatRoom chatRoom = chatRoomRepository.findById(roomIdx)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
        if (!chatRoom.getMember1().getIdx().equals(memberIdx) && !chatRoom.getMember2().getIdx().equals(memberIdx)) {
            throw new SecurityException("Unauthorized access to chat room");
        }

        // 페이징된 메시지 목록 조회
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Slice<Chat> chatSlice = chatRepository.findByRoomIdxAndIsDeleted(roomIdx, pageable);

        // 메시지를 ChatMessageDto로 변환
        List<ChatMessageDto> chatList = chatSlice.getContent().stream()
                .map(ChatMessageDto::fromEntity)
                .collect(Collectors.toList());

        // 읽음 처리
        chatSlice.getContent().stream()
                .filter(chat -> !chat.getIsRead() && chat.getRecipient().getIdx().equals(memberIdx))
                .forEach(Chat::markAsRead);
        chatRepository.saveAll(chatSlice.getContent());

        // ChatRoomReadResponseDto 생성
        ChatRoomReadResponseDto responseDto = ChatRoomReadResponseDto.fromEntity(chatRoom, chatList);

        // 전체 메시지 수 조회
        long totalCount = chatRepository.countByRoomIdxAndIsDeleted(roomIdx);

        // SliceBaseResponse로 반환
        return new SliceBaseResponse<>(
                List.of(responseDto),
                chatSlice.hasNext(),
                totalCount
        );
    }

    public void sendMessage(ChatMessageDto messageDto) {
        Optional<ChatRoom> chatRoomResult = chatRoomRepository.findById(messageDto.getRoomIdx());
        ChatRoom chatRoom = null;
        if (chatRoomResult.isPresent()) {
            chatRoom = chatRoomResult.get();
        }
        chatRepository.save(Chat.from(messageDto, chatRoom));
    }


}
