package com.dabom.member.repository;

import com.dabom.member.model.entity.Member;
import com.dabom.together.model.entity.Together;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByName(String name);
    @Query("SELECT m FROM Member m WHERE m.isDeleted = false AND m.name = :name ORDER BY m.createdAt DESC")
    List<Member> findMembersByName(String name);
}
