package com.dabom.boardcomment.repository;

import com.dabom.boardcomment.model.entity.BoardComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Integer> {

    // 모든 데이터 조회 (관리자용)
    List<BoardComment> findByChannelBoard_Idx(Integer boardIdx);

    // 생성 순서 (오래된 것부터)
    List<BoardComment> findByChannelBoard_IdxAndIsDeletedFalseOrderByIdxAsc(Integer boardIdx);

    // 최신 순서 (새로운 것부터)
    List<BoardComment> findByChannelBoard_IdxAndIsDeletedFalseOrderByIdxDesc(Integer boardIdx);


    // 무한 스크롤용 Slice 메서드 추가
    Slice<BoardComment> findByChannelBoard_IdxAndIsDeletedFalseOrderByIdxAsc(
            Integer boardIdx, Pageable pageable);

    Slice<BoardComment> findByChannelBoard_IdxAndIsDeletedFalseOrderByIdxDesc(
            Integer boardIdx, Pageable pageable);

    // 전체 댓글 개수 조회
    long countByChannelBoard_IdxAndIsDeletedFalse(Integer boardIdx);


}
