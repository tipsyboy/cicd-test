package com.dabom.together.repository;

import com.dabom.member.model.entity.Member;
import com.dabom.together.model.entity.Together;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TogetherRepository extends JpaRepository<Together, Integer> {
    List<Together> findByMaster(Member master);
    Optional<Together> findByCode(UUID code);
    @Query("SELECT t FROM Together t WHERE t.isOpen = true AND t.isDelete = false")
    List<Together> findAllTrue();
    @Query("SELECT t FROM Together t WHERE t.isOpen = true AND t.isDelete = false ORDER BY t.createdAt DESC")
    Slice<Together> findAllByIsOpenTrue(Pageable pageable);
    @Query("SELECT t FROM Together t WHERE t.isOpen = true AND t.isDelete = false AND t.title = :title ORDER BY t.createdAt DESC")
    Slice<Together> searchAllByIsOpenTrue(String title, Pageable pageable);
}
