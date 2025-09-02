package com.dabom.member.security.dto;

import com.dabom.member.model.entity.Member;
import com.dabom.member.model.entity.MemberRole;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class MemberDetailsDto implements UserDetails, OAuth2User {
    private final Integer idx;
    private final String email;
    private final String name;
    private final String password;
    private final MemberRole memberRole;
    private final Boolean isDeleted;
    private final Map<String, Object> attributes;
    private String jwt;

    @Builder
    public MemberDetailsDto(Member member) {
        this.idx = member.getIdx();
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.name = member.getName();
        this.isDeleted = member.getIsDeleted();
        this.memberRole = member.getMemberRole();
        this.attributes = null;
    }

    public static MemberDetailsDto createFromOauth2(Member member, Map<String, Object> attributes) {
        MemberDetailsDto memberDetailsDto = new MemberDetailsDto(member, attributes);
        return memberDetailsDto;
    }

    private MemberDetailsDto(Member member, Map<String, Object> attributes) {
        this.idx = member.getIdx();
        this.email = member.getEmail();
        this.name = member.getName();
        this.password = member.getPassword();
        this.isDeleted = member.getIsDeleted();
        this.memberRole = member.getMemberRole();
        this.attributes = attributes;
    }

    public static MemberDetailsDto createFromToken(Integer idx, String name, String memberRole, String jwt) {
        return new MemberDetailsDto(idx, name, null, memberRole, jwt);
    }

    private MemberDetailsDto(Integer idx, String name, String password, String memberRole, String jwt) {
        this.idx = idx;
        this.email = name;
        this.name = name;
        this.password = password;
        this.isDeleted = false;
        this.memberRole = MemberRole.valueOf(memberRole);
        this.attributes = null;
        this.jwt = jwt;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + memberRole.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }
}
