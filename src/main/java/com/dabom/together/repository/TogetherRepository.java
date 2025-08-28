package com.dabom.together.repository;

import com.dabom.member.model.entity.Member;
import com.dabom.together.model.entity.Together;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TogetherRepository extends JpaRepository<Together, Integer> {
    List<Together> findByMaster(Member master);
    Optional<Together> findByCode(UUID code);
}
