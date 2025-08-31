package com.dabom.together.service;

import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.together.model.dto.request.TogetherChangeMaxMemberRequestDto;
import com.dabom.together.model.dto.request.TogetherChangeTitleRequestDto;
import com.dabom.together.model.dto.request.TogetherCreateRequestDto;
import com.dabom.together.model.dto.response.TogetherInfoResponseDto;
import com.dabom.together.model.dto.response.TogetherListResponseDto;
import com.dabom.together.model.entity.Together;
import com.dabom.together.repository.TogetherJoinMemberRepository;
import com.dabom.together.repository.TogetherRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TogetherServiceTest {
    @InjectMocks
    private TogetherService togetherService;

    @Mock
    private TogetherRepository togetherRepository;
    @Mock
    private TogetherJoinMemberRepository togetherJoinMemberRepository;
    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName(value = "투게더 생성 성공 테스트")
    public void test() {
        // given
        String title = "테스트 제목";
        String videoUrl = "테스트 비디오 경로";
        Integer userIdx = 1;
        Integer maxMemberNum = 10;
        TogetherCreateRequestDto dto = new TogetherCreateRequestDto(
                title,
                videoUrl,
                maxMemberNum,
                true
        );
        MemberDetailsDto memberDetailsDto = MemberDetailsDto.createFromToken(userIdx, "test", "USER");
        Member member = setUpFixtureMember();
        Together together = setUpFixtureTogether(title, videoUrl, member, maxMemberNum);

        // when
        when(memberRepository.findById(userIdx)).thenReturn(Optional.of(member));
        when(togetherRepository.save(dto.toEntity(member))).thenReturn(together);
        TogetherInfoResponseDto responseDto = togetherService.createTogether(dto, memberDetailsDto);

        // then
        assertThat(responseDto.title()).isEqualTo(title);
        assertThat(responseDto.maxMemberNum()).isEqualTo(10);
        assertThat(responseDto.isOpen()).isEqualTo(true);
    }

    @Test
    @DisplayName("오픈된 투게더 리스트 조회 성공 테스트")
    void getTogetherListTest_success() {
        // given
        int page = 0;
        int size = 3;
        Pageable pageable = PageRequest.of(page, size);

        Member member = setUpFixtureMember();
        Together together1 = setUpFixtureTogether("테스트 제목1", "비디오1", member, 10);
        Together together2 = setUpFixtureTogether("테스트 제목2", "비디오2", member, 8);
        List<Together> togetherList = List.of(together1, together2);
        SliceImpl<Together> slice = new SliceImpl<>(togetherList, pageable, false);

        when(togetherRepository.findAllByIsOpenTrue(pageable))
                .thenReturn(slice);

        // when
        TogetherListResponseDto response = togetherService.getTogetherListTest(page, size);

        // then
        assertThat(response.togethers()).hasSize(2);
        assertThat(response.togethers().get(0).title()).isEqualTo("테스트 제목1");
        assertThat(response.togethers().get(1).title()).isEqualTo("테스트 제목2");
    }

    @Test
    @DisplayName("투게더 제목 변경 성공 테스트")
    void changeTogetherTitle_success() {
        // given
        Integer togetherIdx = 1;
        String beforeTitle = "기존 제목";
        String afterTitle = "변경된 제목";

        Member master = setUpFixtureMember();
        Together together = setUpFixtureTogether(beforeTitle, "testUrl", master, 10);

        TogetherChangeTitleRequestDto requestDto = new TogetherChangeTitleRequestDto(afterTitle);
        MemberDetailsDto memberDetailsDto = MemberDetailsDto.createFromToken(1, "test", "USER");

        // mock 설정
        // validMasterMember 내부에서 togetherRepository.findById 등을 사용할 수 있으므로 save만 mock 해둠
        when(togetherRepository.save(together)).thenReturn(together);
        when(togetherRepository.findById(togetherIdx)).thenReturn(Optional.of(together));
        when(memberRepository.findById(1)).thenReturn(Optional.of(master));

        // when
        TogetherInfoResponseDto response = togetherService.changeTogetherTitle(togetherIdx, requestDto, memberDetailsDto);

        // then
        assertThat(response.title()).isEqualTo(afterTitle);
    }

    @Test
    @DisplayName("투게더 최대 인원 변경 성공 테스트")
    public void changeMaxMember_success() {
        // given
        Integer userIdx = 1;
        Integer togetherIdx = 100;
        String title = "테스트 제목";
        String videoUrl = "테스트 비디오 경로";
        Integer oldMaxMemberNum = 10;
        Integer newMaxMemberNum = 20;

        Member master = setUpFixtureMember();
        Together together = setUpFixtureTogether(title, videoUrl, master, oldMaxMemberNum);
        Together changeTogether = setUpFixtureTogether(title, videoUrl, master, newMaxMemberNum);

        MemberDetailsDto memberDetailsDto = MemberDetailsDto.createFromToken(userIdx, "test", "USER");
        TogetherChangeMaxMemberRequestDto requestDto =
                new TogetherChangeMaxMemberRequestDto(newMaxMemberNum);

        // stubbing
        when(togetherRepository.findById(togetherIdx)).thenReturn(Optional.of(together));
        when(togetherRepository.save(together)).thenReturn(changeTogether);
        when(memberRepository.findById(1)).thenReturn(Optional.of(master));

        // when
        TogetherInfoResponseDto responseDto =
                togetherService.changeMaxMember(togetherIdx, requestDto, memberDetailsDto);

        // then
        assertThat(responseDto.maxMemberNum()).isEqualTo(newMaxMemberNum);
    }

    private Member setUpFixtureMember() {
        return Member.builder()
                .name("test")
                .email("test@test.com")
                .memberRole("USER")
                .password("testPassword")
                .build();
    }

    private Together setUpFixtureTogether(String title, String videoUrl, Member member, Integer maxMemberNum) {
        return Together.builder()
                .title(title)
                .videoUrl(videoUrl)
                .master(member)
                .isOpen(true)
                .code(UUID.randomUUID())
                .maxMemberNum(maxMemberNum)
                .isDelete(false)
                .build();
    }
}