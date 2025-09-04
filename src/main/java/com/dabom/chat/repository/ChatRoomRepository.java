package com.dabom.chat.repository;

import com.dabom.chat.model.entity.ChatRoom;
import com.dabom.member.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {

    @Query("SELECT cr FROM ChatRoom cr " +
            "WHERE (cr.member1.idx = :memberIdx OR cr.member2.idx = :memberIdx) " +
            "AND cr.isDeleted = false")
    List<ChatRoom> findAllByMemberIdxAndIsDeleted(@Param("memberIdx") Integer memberIdx);
    Optional<ChatRoom> findByMember1AndMember2OrMember2AndMember1(Member member1, Member member2, Member member3, Member member4);


}
