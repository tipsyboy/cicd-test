package com.dabom.together.service;

import com.dabom.member.exception.MemberException;
import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.together.exception.TogetherException;
import com.dabom.together.model.dto.response.TogetherInfoResponseDto;
import com.dabom.together.model.dto.request.TogetherJoinWithCodeRequestDto;
import com.dabom.together.model.dto.response.TogetherListResponseDto;
import com.dabom.together.model.entity.Together;
import com.dabom.together.model.entity.TogetherJoinMember;
import com.dabom.together.repository.TogetherJoinMemberRepository;
import com.dabom.together.repository.TogetherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.dabom.member.exception.MemberExceptionType.MEMBER_NOT_FOUND;
import static com.dabom.together.exception.TogetherExceptionType.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TogetherJoinMemberService {
    private final TogetherJoinMemberRepository togetherJoinMemberRepository;
    private final TogetherRepository togetherRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public TogetherInfoResponseDto joinNewTogetherMember(Integer togetherIdx, MemberDetailsDto memberDetailsDto) {
        Together together = togetherRepository.findById(togetherIdx)
                .orElseThrow(() -> new TogetherException(NOT_VALID_TOGETHER));
        Member member = memberRepository.findById(memberDetailsDto.getIdx())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Optional<TogetherJoinMember> optional = togetherJoinMemberRepository.findByMemberAndTogether(member, together);

        if(optional.isPresent()) {
            return rollBackTogether(optional.get(), together);
        }

        TogetherJoinMember entity = TogetherJoinMember.builder()
                .member(member)
                .together(together)
                .isJoin(true)
                .isDelete(false)
                .build();
        validTogetherAndSave(entity, together);
        return TogetherInfoResponseDto.toDto(together);
    }

    public TogetherInfoResponseDto joinTogetherMember(Integer togetherIdx, MemberDetailsDto memberDetailsDto) {
        Together together = togetherRepository.findById(togetherIdx).orElseThrow();
        Member member = memberRepository.findById(memberDetailsDto.getIdx()).orElseThrow();
        TogetherJoinMember joinMember =
                togetherJoinMemberRepository.findByMemberAndTogetherAndIsDeleteFalse(member, together)
                        .orElseThrow(() -> new TogetherException(NOT_ACCEPT_MEMBER));

        return TogetherInfoResponseDto.toDtoInJoin(together, member);
    }

    @Transactional
    public TogetherInfoResponseDto joinTogetherWithCodeMember(TogetherJoinWithCodeRequestDto dto, MemberDetailsDto memberDetailsDto) {
        Together together = togetherRepository.findByCode(transformUUID(dto.getCode()))
                .orElseThrow(() -> new TogetherException(NOT_VALID_CODE));
        Member member = memberRepository.findById(memberDetailsDto.getIdx())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Optional<TogetherJoinMember> optional = togetherJoinMemberRepository.findByMemberAndTogether(member, together);

        if(optional.isPresent()) {
            return rollBackTogether(optional.get(), together);
        }

        TogetherJoinMember entity = toEntity(together, member);
        validTogetherAndSave(entity, together);
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

    @Transactional
    public void leaveTogetherMember(Integer togetherIdx, MemberDetailsDto memberDetailsDto) {
        Together together = togetherRepository.findById(togetherIdx).orElseThrow();
        Member member = memberRepository.findById(memberDetailsDto.getIdx()).orElseThrow();

        TogetherJoinMember togetherJoinMember
                = togetherJoinMemberRepository.findByMemberAndTogether(member, together)
                .orElseThrow(() -> new TogetherException(NOT_VALID_TOGETHER));
        if(togetherJoinMember.getIsDelete()) {
            return;
        }
        togetherJoinMember.leaveTogether();
        together.leaveMember();

        togetherRepository.save(together);
        togetherJoinMemberRepository.save(togetherJoinMember);
    }

    private UUID transformUUID(String code) {
        try {
            return UUID.fromString(code);
        } catch (IllegalArgumentException e) {
            throw new TogetherException(NOT_VALID_CODE);
        }
    }

    private TogetherInfoResponseDto rollBackTogether(TogetherJoinMember togetherJoinMember, Together together) {
        if(togetherJoinMember.getIsDelete()) {
            togetherJoinMember.comeBackTogether();
            together.joinMember();
            togetherRepository.save(together);
            togetherJoinMemberRepository.save(togetherJoinMember);
        }
        return TogetherInfoResponseDto.toDto(together);
    }

    private TogetherJoinMember toEntity(Together together, Member member) {
        return TogetherJoinMember.builder()
                .together(together)
                .member(member)
                .isJoin(true)
                .isDelete(false)
                .build();
    }

    private void validTogetherAndSave(TogetherJoinMember entity, Together together) {
        if(together.getMaxMemberNum() > together.getJoinMemberNum()){
            togetherJoinMemberRepository.save(entity);
            together.joinMember();
            togetherRepository.save(together);
            return;
        }
        throw new TogetherException(MAX_TOGETHER_MEMBER);
    }
}
