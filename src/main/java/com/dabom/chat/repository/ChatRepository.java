package com.dabom.chat.repository;

import com.dabom.chat.model.entity.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c WHERE c.room.idx = :roomIdx AND c.isDeleted = false ORDER BY c.createdAt ASC")
    Slice<Chat> findByRoomIdxAndIsDeleted(@Param("roomIdx") Long roomIdx, Pageable pageable);

    @Query("SELECT c FROM Chat c WHERE c.room.idx = :roomIdx AND c.isDeleted = false ORDER BY c.createdAt DESC LIMIT 1")
    Optional<Chat> findTopByRoomIdxAndIsDeletedOrderByCreatedAtDesc(@Param("roomIdx") Long roomIdx);

    @Query("SELECT COUNT(c) FROM Chat c WHERE c.room.idx = :roomIdx AND c.isDeleted = false")
    long countByRoomIdxAndIsDeleted(@Param("roomIdx") Long roomIdx);

    @Query("SELECT COUNT(c) FROM Chat c WHERE c.room.idx = :roomIdx AND c.isDeleted = false AND c.isRead = false AND c.recipient.idx = :memberIdx")
    long countUnreadByRoomIdxAndMemberIdx(@Param("roomIdx") Long roomIdx, @Param("memberIdx") Integer memberIdx);

}
