package com.dabom.channelboard.service;


import com.dabom.boardcomment.model.entity.BoardComment;
import com.dabom.boardcomment.repository.BoardCommentRepository;
import com.dabom.channelboard.exception.ChannelBoardException;
import com.dabom.channelboard.exception.ChannelBoardExceptionType;
import com.dabom.channelboard.model.dto.ChannelBoardReadResponseDto;
import com.dabom.channelboard.model.dto.ChannelBoardRegisterRequestDto;
import com.dabom.channelboard.model.dto.ChannelBoardUpdateRequestDto;
import com.dabom.channelboard.model.entity.ChannelBoard;
import com.dabom.channelboard.repositroy.ChannelBoardRepository;
import com.dabom.common.SliceBaseResponse;
import com.dabom.member.exception.MemberException;
import com.dabom.member.exception.MemberExceptionType;
import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.member.security.dto.MemberDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChannelBoardService {
    private final ChannelBoardRepository channelBoardRepository;
    private final MemberRepository memberRepository;
    private final BoardCommentRepository boardCommentRepository;

    public Integer register(ChannelBoardRegisterRequestDto dto
            , MemberDetailsDto memberDetailsDto) {

        Member memberIdx = memberRepository.findById(memberDetailsDto.getIdx()).
                orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        ChannelBoard result = channelBoardRepository.save(dto.toEntity(memberIdx));
        return result.getIdx();
    }

    public SliceBaseResponse<ChannelBoardReadResponseDto> list(
            Integer page, Integer size, String sort, Integer channelIdx,MemberDetailsDto memberDetailsDto) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<ChannelBoard> channelBoardSlice;

        Integer targetIdx = (channelIdx != null) ? channelIdx : memberDetailsDto.getIdx();

        switch (sort) {
            case "latest":
                channelBoardSlice = channelBoardRepository.findAllByChannelIdxAndIsDeletedFalseOrderByIdxDesc(
                        targetIdx, pageable);
                break;
            case "oldest":
            default:
                channelBoardSlice = channelBoardRepository.findAllByChannelIdxAndIsDeletedFalseOrderByIdxAsc(
                        targetIdx, pageable);
                break;
        }

        List<ChannelBoardReadResponseDto> content = channelBoardSlice.getContent()
                .stream()
                .map(board -> {
                    Long commentCount = channelBoardRepository.countCommentsByBoardIdx(board.getIdx());
                    return ChannelBoardReadResponseDto.fromWithCommentCount(board, commentCount, memberDetailsDto);
                })
                .toList();

        Long totalCount = channelBoardRepository.countByChannelIdxAndIsDeletedFalse(targetIdx);
        return new SliceBaseResponse<ChannelBoardReadResponseDto>(content, channelBoardSlice.hasNext(), totalCount);
    }

    public ChannelBoardReadResponseDto read(Integer idx,  MemberDetailsDto memberDetailsDto) {
        Optional<ChannelBoard> result = channelBoardRepository.findById(idx);
        if (result.isPresent()) {
            ChannelBoard board = result.get();
            Long commentCount = channelBoardRepository.countCommentsByBoardIdx(board.getIdx());
            return ChannelBoardReadResponseDto.fromWithCommentCount(board, commentCount, memberDetailsDto);
        } else {
            throw new ChannelBoardException(ChannelBoardExceptionType.BOARD_NOT_FOUND);        }
    }

    public Integer update(ChannelBoardUpdateRequestDto dto) {
        ChannelBoard result = channelBoardRepository.findById(dto.toEntity().getIdx())
                .orElseThrow(() -> new ChannelBoardException(ChannelBoardExceptionType.BOARD_NOT_FOUND));

        result.update(dto.getTitle(),dto.getContents());

        return channelBoardRepository.save(result).getIdx();
    }

    public void delete(Integer idx) {
        Optional<ChannelBoard> result = channelBoardRepository.findById(idx);

        if (result.isPresent()) {
            ChannelBoard board = result.get();
            ChannelBoardUpdateRequestDto dto = new ChannelBoardUpdateRequestDto();
            ChannelBoard deleteBoard = dto.softDelete(board);
            channelBoardRepository.save(deleteBoard);

            List<BoardComment> boardComments = boardCommentRepository.findByChannelBoard_Idx(idx);
            boardComments.forEach(BoardComment ::delete);
            boardCommentRepository.saveAll(boardComments);

        } else {
            throw new ChannelBoardException(ChannelBoardExceptionType.BOARD_NOT_FOUND);
        }
    }
}