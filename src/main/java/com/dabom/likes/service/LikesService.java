package com.dabom.likes.service;

import com.dabom.likes.model.likes.Likes;
import com.dabom.likes.repository.LikesRepository;
import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.video.model.Video;
import com.dabom.video.repository.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesService {
    private final LikesRepository likesRepository;
    private final VideoRepository videoRepository;
    private final MemberRepository memberRepository;
}
