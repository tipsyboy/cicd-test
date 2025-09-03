package com.dabom.together.service;

import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.together.exception.TogetherException;
import com.dabom.together.model.dto.response.TogetherInfoResponseDto;
import com.dabom.together.model.dto.response.TogetherJoinInfoResponseDto;
import com.dabom.together.model.dto.request.TogetherJoinWithCodeRequestDto;
import com.dabom.together.model.dto.response.TogetherListResponseDto;
import com.dabom.together.model.entity.Together;
import com.dabom.together.model.entity.TogetherJoinMember;
import com.dabom.together.repository.TogetherJoinMemberRepository;
import com.dabom.together.repository.TogetherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.dabom.together.exception.TogetherExceptionType.MAX_TOGETHER_MEMBER;
import static com.dabom.together.exception.TogetherExceptionType.NOT_VALID_CODE;

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
        Together together = togetherRepository.findById(togetherIdx).orElseThrow();
        Member member = memberRepository.findById(memberDetailsDto.getIdx()).orElseThrow();
        Optional<TogetherJoinMember> optional = togetherJoinMemberRepository.findByMemberAndTogether(member, together);

        if(optional.isPresent()) {
            return isSoftDeleted(optional.get(), together);
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
                        .orElseThrow();

        return TogetherInfoResponseDto.toDtoInJoin(together, member);
    }

    @Transactional
    public TogetherInfoResponseDto joinTogetherWithCodeMember(TogetherJoinWithCodeRequestDto dto, MemberDetailsDto memberDetailsDto) {
        Together together = togetherRepository.findByCode(transformUUID(dto.getCode()))
                .orElseThrow(() -> new TogetherException(NOT_VALID_CODE));
        Member member = memberRepository.findById(memberDetailsDto.getIdx()).orElseThrow();
        Optional<TogetherJoinMember> optional = togetherJoinMemberRepository.findByMemberAndTogether(member, together);

        if(optional.isPresent()) {
            return isSoftDeleted(optional.get(), together);
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
        together.leaveMember();

        togetherRepository.save(together);
        togetherJoinMemberRepository.save(togetherJoinMember);
    }

    @Transactional
    @Scheduled(cron = "0 10 0 * * *")
    public void deleteSoftDeletedMembers() {
        log.info("함께하기 멤버 삭제 스케줄 시작: {}", LocalDateTime.now());
        // 방법 1: find & delete each
        List<TogetherJoinMember> toDelete = togetherJoinMemberRepository.findByIsDeleteTrue();
        if (!toDelete.isEmpty()) {
            togetherJoinMemberRepository.deleteAll(toDelete);
            log.info("Soft-deleted TogetherJoinMember {}건 영구 삭제 완료", toDelete.size());
        } else {
            log.info("삭제 대상 없음");
        }

        // 방법 2: bulk delete 쿼리 사용 시
        // int deletedCount = repository.deleteAllByIsDeleteTrue();
        // log.info("Bulk delete 완료: {}건", deletedCount);
    }

    private UUID transformUUID(String code) {
        try {
            return UUID.fromString(code);
        } catch (IllegalArgumentException e) {
            throw new TogetherException(NOT_VALID_CODE);
        }
    }

    private TogetherInfoResponseDto isSoftDeleted(TogetherJoinMember togetherJoinMember, Together together) {
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
