package com.dabom.boardcomment.service;

import com.dabom.boardcomment.model.dto.BoardCommentCreateRequestDto;
import com.dabom.boardcomment.model.dto.BoardCommentResponseDto;
import com.dabom.boardcomment.model.entity.BoardComment;
import com.dabom.boardcomment.repository.BoardCommentRepository;
import com.dabom.channelboard.model.entity.ChannelBoard;
import com.dabom.channelboard.repositroy.ChannelBoardRepository;
import com.dabom.common.SliceBaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardCommentService {

    private final BoardCommentRepository boardCommentRepository;
    private final ChannelBoardRepository channelBoardRepository;

    public Integer create(BoardCommentCreateRequestDto dto, Integer boardIdx, MemberDetailsDto memberDetailsDto) {
        ChannelBoard board = channelBoardRepository.findById(boardIdx)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + boardIdx));

        BoardComment comment = dto.toEntity(board);
        return boardCommentRepository.save(comment).getIdx();
    }

    public void delete(Integer commentIdx) {
        BoardComment entity = boardCommentRepository.findById(commentIdx)
                .orElseThrow(() -> new EntityNotFoundException("댓글 찾을 수 없음"));
        entity.delete();
        boardCommentRepository.save(entity);
    }

    public List<BoardCommentResponseDto> list(Integer boardIdx, String sortBy, MemberDetailsDto memberDetailsDto) {
        List<BoardComment> comments;

        switch (sortBy) {
            case "latest":
                comments = boardCommentRepository
                        .findByChannelBoard_IdxAndIsDeletedFalseOrderByIdxDesc(boardIdx);
                break;
            case "oldest":
                comments = boardCommentRepository
                        .findByChannelBoard_IdxAndIsDeletedFalseOrderByIdxAsc(boardIdx);
                break;
            default:
                comments = boardCommentRepository
                        .findByChannelBoard_IdxAndIsDeletedFalseOrderByIdxAsc(boardIdx);
        }

        return comments.stream()
                .map(comment -> BoardCommentResponseDto.from(comment, memberDetailsDto))
                .toList();
    }

    public List<BoardCommentResponseDto> list(Integer boardIdx) {
        return list(boardIdx, "oldest", null);  // 기본값: 오래된 순, 로그인하지 않은 사용자
    }

    public BoardCommentResponseDto update(Integer boardCommentIdx, BoardCommentCreateRequestDto dto, MemberDetailsDto memberDetailsDto) {
        BoardComment comment = boardCommentRepository.findById(boardCommentIdx)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        comment.updateContent(dto.getContent());
        BoardComment updatedComment = boardCommentRepository.save(comment);
        return BoardCommentResponseDto.from(updatedComment, memberDetailsDto);
    }

    public SliceBaseResponse<BoardCommentResponseDto> getPagedComments(
            Integer boardIdx, Integer page, Integer size, String sort, MemberDetailsDto memberDetailsDto) {

        Pageable pageable = PageRequest.of(page, size);
        Slice<BoardComment> commentSlice;

        switch (sort) {
            case "latest":
                commentSlice = boardCommentRepository
                        .findByChannelBoard_IdxAndIsDeletedFalseOrderByIdxDesc(boardIdx, pageable);
                break;
            case "oldest":
            default:
                commentSlice = boardCommentRepository
                        .findByChannelBoard_IdxAndIsDeletedFalseOrderByIdxAsc(boardIdx, pageable);
                break;
        }

        List<BoardCommentResponseDto> content = commentSlice.getContent()
                .stream()
                .map(comment -> BoardCommentResponseDto.from(comment, memberDetailsDto))
                .toList();

        long totalCount = boardCommentRepository.countByChannelBoard_IdxAndIsDeletedFalse(boardIdx);
        return new SliceBaseResponse<BoardCommentResponseDto>(content, commentSlice.hasNext(), totalCount);
    }
}