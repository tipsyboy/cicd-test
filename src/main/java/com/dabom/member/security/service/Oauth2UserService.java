package com.dabom.member.security.service;

import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.member.utils.RandomStringCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Oauth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository repository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String social = userRequest.getClientRegistration().getRegistrationId();
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        return createOAuth2User(social, attributes);
    }

    private MemberDetailsDto createOAuth2User(String social, Map<String, Object> attributes) {
        return switch (social) {
            case "google" -> loginGoogle(attributes);
            case "kakao"  -> loginKakao(attributes);
            default       -> loginNaver(attributes);
        };
    }

    private MemberDetailsDto loginNaver(Map<String, Object> attributes) {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("response");

        String name = properties.get("name") + RandomStringCreator.createRandomString();
        String id = properties.get("id").toString();
        Member member = loginOrSignupMember(id, name);
        return MemberDetailsDto.createFromOauth2(member, attributes);
    }

    private MemberDetailsDto loginKakao(Map<String, Object> attributes) {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        String name = properties.get("nickname") + RandomStringCreator.createRandomString();
        String id = attributes.get("id").toString();
        Member member = loginOrSignupMember(id, name);
        return MemberDetailsDto.createFromOauth2(member, attributes);
    }

    private MemberDetailsDto loginGoogle(Map<String, Object> attributes) {
        String name = attributes.get("name") + RandomStringCreator.createRandomString();
        String id = attributes.get("email").toString();
        Member member = loginOrSignupMember(id, name);
        return MemberDetailsDto.createFromOauth2(member, attributes);
    }

    private Member loginOrSignupMember(String id, String name) {
        Optional<Member> optionalMember = repository.findByEmail(id);
        if(optionalMember.isEmpty()) {
            Member newMember = Member.builder()
                    .email(id)
                    .password(null)
                    .memberRole("USER")
                    .name(name)
                    .build();
            return repository.save(newMember);
        }
        Member member = optionalMember.get();
        if(member.getIsDeleted()) {
            member.rollBackMember();
            return repository.save(member);
        }
        return member;
    }
}

