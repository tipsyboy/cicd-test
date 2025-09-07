package com.dabom.videocomment.service;

import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.member.security.dto.MemberDetailsDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VideoCommentService {

    private final VideoCommentRepository videoCommentRepository;
    private final VideoRepository videoRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    private static final String DEFAULT_PROFILE_IMAGE = "/Image/Dabompng.png";

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

    public Slice<VideoCommentResponseDto> list(Integer videoIdx, Pageable pageable, MemberDetailsDto memberDetailsDto) {
        Slice<VideoComment> result;
        if (pageable.getSort().getOrderFor("likes") != null) {
            result = videoCommentRepository.findByVideo_IdxAndIsDeletedFalseOrderByLikesDesc(videoIdx, pageable);
        } else {
            result = videoCommentRepository.findByVideo_IdxAndIsDeletedFalse(videoIdx, pageable);
        }

        return result.map(comment -> {
            String profileImg = getProfileImgSafely(comment);
            return VideoCommentResponseDto.from(comment, profileImg, memberDetailsDto);
        });
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

    private String getProfileImgSafely(VideoComment comment) {
        try {
            if (comment.getMember() != null) {
                String profileImg = memberService.getProfileImg(comment.getMember().getIdx());
                if (profileImg != null && !profileImg.trim().isEmpty()) {
                    return profileImg;
                }
            }
        } catch (Exception e) {
            log.warn("프로필 이미지 조회 실패");
        }
        return DEFAULT_PROFILE_IMAGE;
    }
}