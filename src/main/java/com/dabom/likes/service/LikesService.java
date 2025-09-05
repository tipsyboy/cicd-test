package com.dabom.likes.service;

import com.dabom.boardcomment.model.entity.BoardComment;
import com.dabom.boardcomment.repository.BoardCommentRepository;
import com.dabom.channelboard.model.entity.ChannelBoard;
import com.dabom.channelboard.repositroy.ChannelBoardRepository;
import com.dabom.likes.model.likes.Likes;
import com.dabom.likes.repository.LikesRepository;
import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.videocomment.model.entity.VideoComment;
import com.dabom.videocomment.repository.VideoCommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesService {
    private final LikesRepository likesRepository;
    private final MemberRepository memberRepository;

    private final BoardCommentRepository boardCommentRepository;
    private final ChannelBoardRepository channelBoardRepository;
    private final VideoCommentRepository videoCommentRepository;

    public void handleBoardCommentLike(MemberDetailsDto memberDetailsDto, Integer Idx) {
        BoardComment boardComment = boardCommentRepository.findById(Idx).orElseThrow(()->new EntityNotFoundException(""));
        Member member = memberRepository.findById(memberDetailsDto.getIdx()).orElseThrow(()->new EntityNotFoundException(""));

        Optional<Likes> result =  likesRepository.findByChannelAndBoardComment(member, boardComment);
        if(result.isPresent()) {
            likesRepository.delete(result.get());
            boardComment.decrementLikeCount();
        } else {
            Likes likes = Likes.builder()
                    .channel(member)
                    .boardComment(boardComment)
                    .build();
            likesRepository.save(likes);
            boardComment.incrementLikeCount();
        }
        boardCommentRepository.save(boardComment);
    }

    public void handleChannelBoardLike(MemberDetailsDto memberDetailsDto, Integer Idx) {
        ChannelBoard channelBoard = channelBoardRepository.findById(Idx).orElseThrow(()->new EntityNotFoundException(""));
        Member member = memberRepository.findById(memberDetailsDto.getIdx()).orElseThrow(()->new EntityNotFoundException(""));

        Optional<Likes> result =  likesRepository.findByChannelAndChannelBoard(member, channelBoard);
        if(result.isPresent()) {
            likesRepository.delete(result.get());
            channelBoard.decrementLikeCount();
        } else {
            Likes likes = Likes.builder()
                    .channel(member)
                    .channelBoard(channelBoard)
                    .build();
            likesRepository.save(likes);
            channelBoard.incrementLikeCount();
        }
        channelBoardRepository.save(channelBoard);
    }

    public void handelVideoCommentLike(MemberDetailsDto memberDetailsDto, Integer idx) {
        VideoComment videoComment = videoCommentRepository.findById(idx).orElseThrow( () -> new EntityNotFoundException(""));
        Member member = memberRepository.findById(memberDetailsDto.getIdx()).orElseThrow(() -> new EntityNotFoundException(""));

        Optional<Likes> result = likesRepository.findByChannelAndVideoComment(member,videoComment);
        if(result.isPresent()){
            likesRepository.delete(result.get());
            videoComment.decrementLikeCount();
        } else{
            Likes likes = Likes.builder()
                    .channel(member)
                    .videoComment(videoComment)
                    .build();
            likesRepository.save(likes);
            videoComment.incrementLikeCount();
        }
        videoCommentRepository.save(videoComment);

    }

}
