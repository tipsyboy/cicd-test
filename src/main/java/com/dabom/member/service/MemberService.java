package com.dabom.member.service;

import com.dabom.member.exception.MemberException;
import com.dabom.member.model.dto.*;
import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.member.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.dabom.member.exception.MemberExceptionType.DUPLICATED_CHANNEL_NAME;
import static com.dabom.member.exception.MemberExceptionType.MEMBER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository repository;
    private final AuthenticationManager manager;
    private final PasswordEncoder encoder;

    @Transactional
    public void signUpMember(MemberSignupRequestDto dto) {
        String encodedPassword = encoder.encode(dto.getPassword());
        checkDuplicatedName(dto);
        repository.save(dto.toEntity(encodedPassword));
    }

    public String loginMember(MemberLoginRequestDto dto) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword(), null);
        Authentication authenticate = manager.authenticate(token);
        MemberDetailsDto userDto = (MemberDetailsDto) authenticate.getPrincipal();

        return JwtUtils.generateLoginToken(userDto.getIdx(), userDto.getEmail(), userDto.getMemberRole());
    }

    public MemberListResponseDto searchMemberName(MemberSearchRequestDto dto) {
        List<Member> members = repository.findMembersByName(dto.getName());
        return MemberListResponseDto.toDto(members);
    }

    public MemberInfoResponseDto readMemberInfo(MemberDetailsDto dto) {
        Member member = getMemberFromSecurity(dto);

        return MemberInfoResponseDto.toDto(member);
    }

    public MemberEmailCheckResponseDto checkMemberEmail(String email) {
        Optional<Member> optionalMember = repository.findByEmail(email);
        if(optionalMember.isEmpty()) {
            return MemberEmailCheckResponseDto.of(false);
        }
        return MemberEmailCheckResponseDto.of(true);
    }

    public MemberChannelNameCheckResponseDto checkMemberChannelName(String channelName) {
        Optional<Member> optionalMember = repository.findByName(channelName);
        if(optionalMember.isEmpty()) {
            return MemberChannelNameCheckResponseDto.of(false);
        }
        return MemberChannelNameCheckResponseDto.of(true);
    }

    public MemberInfoResponseDto getMemberInfo(MemberDetailsDto dto) {
        Member member = repository.findById(dto.getIdx()).orElseThrow();
        return MemberInfoResponseDto.toDto(member);
    }

    @Transactional
    public void updateMemberName(MemberDetailsDto memberDetailsDto, MemberUpdateChannelRequestDto dto) {
        Member member = getMemberFromSecurity(memberDetailsDto);
        updateChannelName(dto, member);
        updateChannelContent(dto, member);
        repository.save(member);
    }

    @Transactional
    public void deleteMember(MemberDetailsDto dto) {
        Member member = getMemberFromSecurity(dto);
        member.deleteMember();
        repository.save(member);
    }

    private void checkDuplicatedName(MemberSignupRequestDto dto) {
        Optional<Member> checkName = repository.findByName(dto.getChannelName());
        if(checkName.isEmpty()) {
            return;
        }
        throw new MemberException(DUPLICATED_CHANNEL_NAME);
    }

    private void updateChannelContent(MemberUpdateChannelRequestDto dto, Member member) {
        if(dto.getContent() != null) {
            member.updateContent(dto.getContent());
        }
    }

    private void updateChannelName(MemberUpdateChannelRequestDto dto, Member member) {
        if(dto.getName() != null) {
            checkDuplicateName(dto);
            member.updateName(dto.getName());
        }
    }

    private void checkDuplicateName(MemberUpdateChannelRequestDto dto) {
        MemberChannelNameCheckResponseDto check = checkMemberChannelName(dto.getName());
        if(check.isDuplicate()) {
            throw new MemberException(DUPLICATED_CHANNEL_NAME);
        }
    }

    private Member getMemberFromSecurity(MemberDetailsDto dto) {
        Integer idx = dto.getIdx();
        Optional<Member> optionalMember = repository.findById(idx);
        if(optionalMember.isEmpty()) {
            throw new MemberException(MEMBER_NOT_FOUND);
        }
        return optionalMember.get();
    }
}
