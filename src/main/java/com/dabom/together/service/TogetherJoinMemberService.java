package com.dabom.together.service;

import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.together.model.dto.response.TogetherInfoResponseDto;
import com.dabom.together.model.dto.response.TogetherJoinInfoResponseDto;
import com.dabom.together.model.dto.request.TogetherJoinWithCodeRequestDto;
import com.dabom.together.model.dto.response.TogetherListResponseDto;
import com.dabom.together.model.entity.Together;
import com.dabom.together.model.entity.TogetherJoinMember;
import com.dabom.together.repository.TogetherJoinMemberRepository;
import com.dabom.together.repository.TogetherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TogetherJoinMemberService {
    private final TogetherJoinMemberRepository togetherJoinMemberRepository;
    private final TogetherRepository togetherRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public TogetherInfoResponseDto joinNewTogetherMember(Integer togetherIdx, MemberDetailsDto memberDetailsDto) {
        Together together = togetherRepository.findById(togetherIdx).orElseThrow();
        Member member = memberRepository.findById(memberDetailsDto.getIdx()).orElseThrow();
        Optional<TogetherJoinMember> optional = togetherJoinMemberRepository.findByMemberAndTogether(member, together);
        if(optional.isPresent()) {
            return TogetherInfoResponseDto.toDto(together);
        }

        TogetherJoinMember entity = TogetherJoinMember.builder()
                .member(member)
                .together(together)
                .isJoin(true)
                .isDelete(false)
                .build();

        togetherJoinMemberRepository.save(entity);
        together.joinMember();
        togetherRepository.save(together);

        return TogetherInfoResponseDto.toDto(together);
    }

    public TogetherInfoResponseDto joinTogetherMember(Integer togetherIdx, MemberDetailsDto memberDetailsDto) {
        Together together = togetherRepository.findById(togetherIdx).orElseThrow();
        Member member = memberRepository.findById(memberDetailsDto.getIdx()).orElseThrow();
        TogetherJoinMember joinMember =
                togetherJoinMemberRepository.findByMemberAndTogetherAndIsDeleteFalse(member, together).orElseThrow();

        return TogetherInfoResponseDto.toDtoInJoin(together, member);
    }

    @Transactional
    public TogetherInfoResponseDto joinTogetherWithCodeMember(Integer togetherIdx, TogetherJoinWithCodeRequestDto dto, MemberDetailsDto memberDetailsDto) {
        Together together = togetherRepository.findById(togetherIdx).orElseThrow();
        Member member = memberRepository.findById(memberDetailsDto.getIdx()).orElseThrow();
        Optional<TogetherJoinMember> optional = togetherJoinMemberRepository.findByMemberAndTogether(member, together);
        if(optional.isPresent()) {
            return TogetherInfoResponseDto.toDto(together);
        }

        TogetherJoinMember entity = toEntity(together, member);
        validInvitedCode(dto, together);
        joinTogetherMember(entity, together);
        return TogetherInfoResponseDto.toDto(together);
    }

    public TogetherListResponseDto getTogethersFromMember(MemberDetailsDto memberDetailsDto) {
        Member member = memberRepository.findById(memberDetailsDto.getIdx()).orElseThrow();
        List<TogetherJoinMember> togethers = togetherJoinMemberRepository.findByMemberAndIsDeleteFalse(member);
        List<Together> togetherList = togethers.stream()
                .map(TogetherJoinMember::getTogether)
                .toList();
        return TogetherListResponseDto.toDto(togetherList);
    }

    public TogetherListResponseDto getTogethersFromMaster(MemberDetailsDto memberDetailsDto) {
        Member member = memberRepository.findById(memberDetailsDto.getIdx()).orElseThrow();
        List<Together> togethers = togetherRepository.findByMaster(member);

        return TogetherListResponseDto.toDto(togethers);
    }

    public TogetherJoinInfoResponseDto loginTogetherMember(Integer togetherIdx, Member member) {
        Together together = togetherRepository.findById(togetherIdx).orElseThrow();
        TogetherJoinMember togetherJoinMember
                = togetherJoinMemberRepository.findByMemberAndTogetherAndIsDeleteFalse(member, together).orElseThrow();

        return TogetherJoinInfoResponseDto.toDto(together);
    }

    @Transactional
    public void leaveTogetherMember(Integer togetherIdx, MemberDetailsDto memberDetailsDto) {
        Together together = togetherRepository.findById(togetherIdx).orElseThrow();
        Member member = memberRepository.findById(memberDetailsDto.getIdx()).orElseThrow();

        TogetherJoinMember togetherJoinMember
                = togetherJoinMemberRepository.findByMemberAndTogetherAndIsDeleteFalse(member, together).orElseThrow();
        togetherJoinMember.leaveTogether();

        togetherJoinMemberRepository.save(togetherJoinMember);
    }

    private TogetherJoinMember toEntity(Together together, Member member) {
        return TogetherJoinMember.builder()
                .together(together)
                .member(member)
                .isJoin(true)
                .isDelete(false)
                .build();
    }

    private void isPublicTogether(Together together) {
        if(!together.getIsOpen()) {
            throw new RuntimeException();
        }
    }

    private void joinTogetherMember(TogetherJoinMember entity, Together together) {
        if(together.getMaxMemberNum() > together.getJoinMemberNum()){
            togetherJoinMemberRepository.save(entity);
            together.joinMember();
            togetherRepository.save(together);
        }
    }

    private void validInvitedCode(TogetherJoinWithCodeRequestDto dto, Together together) {
        if(!together.getInvitedCode().equals(dto.getCode())) {
            throw new RuntimeException();
        }
    }
}
