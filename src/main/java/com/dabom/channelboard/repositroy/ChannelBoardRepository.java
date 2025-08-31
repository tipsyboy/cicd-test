package com.dabom.channelboard.repositroy;

import com.dabom.channelboard.model.entity.ChannelBoard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ChannelBoardRepository extends JpaRepository<ChannelBoard, Integer> {

    Slice<ChannelBoard> findAllByChannelIdxAndIsDeletedFalseOrderByIdxAsc(
            Integer channelIdx, Pageable pageable);

    Slice<ChannelBoard> findAllByChannelIdxAndIsDeletedFalseOrderByIdxDesc(
            Integer channelIdx, Pageable pageable);

    Long countByChannelIdxAndIsDeletedFalse(Integer channelIdx);

    @Query("SELECT COUNT(bc) FROM BoardComment bc " +
           "WHERE bc.channelBoard.idx = :boardIdx AND bc.isDeleted = false")
    Long countCommentsByBoardIdx(Integer boardIdx);
}
