package com.dabom.channelboard.repositroy;

import com.dabom.channelboard.model.entity.ChannelBoard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ChannelBoardRepository extends JpaRepository<ChannelBoard, Integer> {

    @Query("SELECT cb FROM ChannelBoard cb " +
           "JOIN cb.channel c " +
           "WHERE c.name = :channelName " +
           "AND cb.isDeleted = false " +
           "ORDER BY cb.idx ASC")
    Slice<ChannelBoard> findAllByChannelNameAndIsDeletedFalseOrderByIdxAsc(
            @Param("channelName") String channelName,
            Pageable pageable);


    @Query("SELECT cb FROM ChannelBoard cb " +
           "JOIN cb.channel c " +
           "WHERE c.name = :channelName " +
           "AND cb.isDeleted = false " +
           "ORDER BY cb.idx DESC")
    Slice<ChannelBoard> findAllByChannelNameAndIsDeletedFalseOrderByIdxDesc(
            @Param("channelName") String channelName,
            Pageable pageable);

    @Query("SELECT COUNT(cb) FROM ChannelBoard cb " +
           "JOIN cb.channel c " +
           "WHERE c.name = :channelName " +
           "AND cb.isDeleted = false")
    Long countByChannelNameAndIsDeletedFalse(@Param("channelName") String channelName);

    @Query("SELECT COUNT(bc) FROM BoardComment bc " +
           "WHERE bc.channelBoard.idx = :boardIdx AND bc.isDeleted = false")
    Long countCommentsByBoardIdx(Integer boardIdx);



}
