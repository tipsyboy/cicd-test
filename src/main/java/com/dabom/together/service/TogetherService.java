package com.dabom.together.service;

import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.together.model.dto.request.*;
import com.dabom.together.model.dto.response.TogetherInfoResponseDto;
import com.dabom.together.model.dto.response.TogetherListResponseDto;
import com.dabom.together.model.dto.response.TogetherMasterResponseDto;
import com.dabom.together.model.dto.response.TogetherMemberListResponseDto;
import com.dabom.together.model.entity.Together;
import com.dabom.together.model.entity.TogetherJoinMember;
import com.dabom.together.repository.TogetherJoinMemberRepository;
import com.dabom.together.repository.TogetherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TogetherService {
    private final TogetherRepository togetherRepository;
    private final TogetherJoinMemberRepository togetherJoinMemberRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public TogetherInfoResponseDto createTogether(TogetherCreateRequestDto dto, MemberDetailsDto memberDetailsDto) {
        Member member = memberRepository.findById(memberDetailsDto.getIdx()).orElseThrow();
        Together together = togetherRepository.save(dto.toEntity(member));
        TogetherJoinMember togetherJoinMember = TogetherJoinMember.builder()
                .isJoin(true)
                .isDelete(false)
                .member(member)
                .together(together)
                .build();
        togetherJoinMemberRepository.save(togetherJoinMember);
        return TogetherInfoResponseDto.toDto(together);
    }

    public TogetherListResponseDto getTogetherList() {
        List<Together> togethers = togetherRepository.findAll();
        return TogetherListResponseDto.toDto(togethers);
    }

    public TogetherListResponseDto searchTogetherList(TogetherSearchRequestDto dto) {
        List<Together> togethers = togetherRepository.findAll();
        return TogetherListResponseDto.toDto(togethers);
    }

    @Transactional
    public TogetherInfoResponseDto changeTogetherTitle(Integer togetherIdx,
                                                       TogetherChangeTitleRequestDto dto,
                                                       MemberDetailsDto memberDetailsDto) {
        Together together = validMasterMember(togetherIdx, memberDetailsDto);
        together.changeTitle(dto.getTitle());
        Together save = togetherRepository.save(together);
        return TogetherInfoResponseDto.toDto(save);
    }

    @Transactional
    public TogetherInfoResponseDto changeMaxMember(Integer togetherIdx, TogetherChangeMaxMemberRequestDto dto,
                                MemberDetailsDto memberDetailsDto) {
        Together together = validMasterMember(togetherIdx, memberDetailsDto);

        together.changeMaxMemberNumber(dto.getMaxMember());
        Together save = togetherRepository.save(together);

        return TogetherInfoResponseDto.toDto(save);
    }

    @Transactional
    public TogetherInfoResponseDto changeIsOpen(Integer togetherIdx, TogetherChangeIsOpenRequestDto dto,
                                                   MemberDetailsDto memberDetailsDto) {
        Together together = validMasterMember(togetherIdx, memberDetailsDto);

        together.changeTogetherIsOpen(dto.getIsOpen());
        Together save = togetherRepository.save(together);

        return TogetherInfoResponseDto.toDto(save);
    }

    public TogetherMemberListResponseDto getTogetherMembersFromMaster(Integer togetherIdx, MemberDetailsDto memberDetailsDto) {
        Together together = validMasterMember(togetherIdx, memberDetailsDto);

        return TogetherMemberListResponseDto.toDto(together);
    }

    public TogetherMasterResponseDto isMaster(Integer togetherIdx, MemberDetailsDto memberDetailsDto) {
        Together together = validMasterMember(togetherIdx, memberDetailsDto);

        return TogetherMasterResponseDto.isMasterMember();
    }

    @Transactional
    public TogetherInfoResponseDto changeVideo(Integer togetherIdx, TogetherChangeVideoRequestDto dto,
                                                MemberDetailsDto memberDetailsDto) {
        Together together = validMasterMember(togetherIdx, memberDetailsDto);

        together.changeVideo(dto.getVideoUrl());
        Together save = togetherRepository.save(together);

        return TogetherInfoResponseDto.toDto(save);
    }

    @Transactional
    public TogetherInfoResponseDto kickTogetherMember(Integer togetherIdx, Integer memberIdx,
                                   MemberDetailsDto memberDetailsDto) {
        Together together = validMasterMember(togetherIdx, memberDetailsDto);

        Member member = memberRepository.findById(memberIdx).orElseThrow();
        TogetherJoinMember kickMember = togetherJoinMemberRepository.findByMemberAndTogetherAndIsDeleteFalse(member, together).orElseThrow();
        kickMember.expel();

        togetherJoinMemberRepository.save(kickMember);
        together.leaveMember();
        Together save = togetherRepository.save(together);
        return TogetherInfoResponseDto.toDto(save);
    }

    @Transactional
    public void deleteTogether(Integer togetherIdx, MemberDetailsDto memberDetailsDto) {
        Together together = validMasterMember(togetherIdx, memberDetailsDto);

        together.deleteTogether();
        togetherRepository.save(together);
    }

    private Together validMasterMember(Integer togetherIdx, MemberDetailsDto memberDetailsDto) {
        Together together = togetherRepository.findById(togetherIdx).orElseThrow();
        Member member = memberRepository.findById(memberDetailsDto.getIdx()).orElseThrow();
        if(!together.getMaster().equals(member)) {
            throw new RuntimeException();
        }
        return together;
    }
}
