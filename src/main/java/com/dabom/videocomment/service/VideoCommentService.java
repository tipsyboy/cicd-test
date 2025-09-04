package com.dabom.videocomment.service;

import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.member.service.MemberService;
import com.dabom.video.model.Video;
import com.dabom.video.repository.VideoRepository;
import com.dabom.videocomment.model.dto.VideoCommentRegisterDto;
import com.dabom.videocomment.model.dto.VideoCommentResponseDto;
import com.dabom.videocomment.model.dto.VideoCommentUpdateDto;
import com.dabom.videocomment.model.entity.VideoComment;
import com.dabom.videocomment.repository.VideoCommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VideoCommentService {

    private final VideoCommentRepository videoCommentRepository;
    private final VideoRepository videoRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @Transactional
    public Integer register(VideoCommentRegisterDto dto, Integer videoIdx, Integer memberIdx) {
        Video video = videoRepository.findById(videoIdx)
                .orElseThrow(() -> new EntityNotFoundException("영상을 찾을 수 없습니다: " + videoIdx));

        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다: " + memberIdx));

        VideoComment videoComment = dto.toEntity(video, member);
        return videoCommentRepository.save(videoComment).getIdx();
    }

    @Transactional
    public void deleted(Integer idx) {
        VideoComment videoComment = videoCommentRepository.findById(idx)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다: " + idx));
        videoComment.delete();
        videoCommentRepository.save(videoComment);
    }

    public Slice<VideoCommentResponseDto> list(Integer videoIdx, Pageable pageable) {
        Slice<VideoComment> result;
        if (pageable.getSort().getOrderFor("likes") != null) {
            result = videoCommentRepository.findByVideo_IdxAndIsDeletedFalseOrderByLikesDesc(videoIdx, pageable);
        } else {
            result = videoCommentRepository.findByVideo_IdxAndIsDeletedFalse(videoIdx, pageable);
        }

        return result.map(comment -> VideoCommentResponseDto.from(comment, memberService));
    }

    @Transactional
    public Integer update(Integer commentIdx, VideoCommentUpdateDto dto) {
        VideoComment entity = videoCommentRepository.findById(commentIdx)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다: " + commentIdx));

        dto.toEntity(entity);
        videoCommentRepository.save(entity);

        return entity.getIdx();
    }

    public VideoComment findById(Integer commentIdx) {
        return videoCommentRepository.findById(commentIdx)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다: " + commentIdx));
    }
}