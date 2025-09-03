package com.dabom.member.security.service;

import com.dabom.member.exception.MemberException;
import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.member.security.dto.MemberDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.dabom.member.exception.MemberExceptionType.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CustomMemberDetailsService implements UserDetailsService {
    private final MemberRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> optionalMember = repository.findByEmail(username);
        if(optionalMember.isEmpty()) {
            throw new MemberException(MEMBER_NOT_FOUND);
        }
        Member member = optionalMember.get();
        if(member.getIsDeleted()) {
            member.rollBackMember();
            member = repository.save(member);
        }
        return MemberDetailsDto.builder()
                .member(member)
                .build();
    }
}
