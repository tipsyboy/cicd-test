package com.dabom.member.controller;

import com.dabom.common.BaseResponse;
import com.dabom.member.model.dto.request.*;
import com.dabom.member.model.dto.response.MemberChannelNameCheckResponseDto;
import com.dabom.member.model.dto.response.MemberEmailCheckResponseDto;
import com.dabom.member.model.dto.response.MemberInfoResponseDto;
import com.dabom.member.model.dto.response.MemberLoginResponseDto;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.dabom.member.contants.MemberSwaggerConstants.*;
import static com.dabom.member.contants.JWTConstants.ACCESS_TOKEN;

@Tag(name = "멤버 관리 기능")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @Operation(
            summary = "멤버 회원 가입",
            description = "새로운 회원을 등록합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "가입할 멤버의 채널 이름, 이메일, 비밀번호, 유저 타입",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemberSignupRequestDto.class),
                            examples = @ExampleObject(
                                    name = "멤버 회원 가입 예시",
                                    value = SIGNUP_MEMBER_REQUEST
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "멤버 가입 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(
                                            name = "멤버 회원 가입 성공 응답",
                                            value = SIGNUP_MEMBER_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "이미 가입된 회원이거나 제한사항을 넘는 길이의 멤버 가입 요청",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<String>> signupMember(@Valid @RequestBody MemberSignupRequestDto dto) {
        memberService.signUpMember(dto);
        return ResponseEntity.ok(BaseResponse.of("회원 가입 성공", HttpStatus.OK));
    }

    @Operation(
            summary = "멤버 로그인",
            description = "로그인을 진행합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "가입한 멤버의 이메일 및 비밀번호",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemberLoginRequestDto.class),
                            examples = @ExampleObject(
                                    name = "멤버 로그인 예시",
                                    value = LOGIN_MEMBER_REQUEST
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "멤버 로그인 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(
                                            name = "멤버 로그인 성공 응답",
                                            value = LOGIN_MEMBER_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "가입하지 않은 이메일이거나 비밀번호를 잘 못 입력",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<String>> loginMember(@Valid @RequestBody MemberLoginRequestDto dto) {
        MemberLoginResponseDto responseDto = memberService.loginMember(dto);
        if (responseDto.jwt() != null) {
            ResponseCookie accessTokenCookie = ResponseCookie.from(ACCESS_TOKEN, responseDto.jwt())
                    .httpOnly(true)
                    .secure(false)
//                    .sameSite("None")
                    .path("/")
                    .maxAge(60 * 60) // 1시간
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .body(BaseResponse.of(responseDto.channelName(), HttpStatus.OK));
        }
        return ResponseEntity.status(400).body(BaseResponse.of("로그인 실패", HttpStatus.BAD_REQUEST));
    }

    @Operation(
            summary = "멤버 로그아웃",
            description = "로그아웃을 진행합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "없음",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(),
                            examples = @ExampleObject(
                                    name = "멤버 로그아웃 예시",
                                    value = ""
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "멤버 로그아웃 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(
                                            name = "멤버 로그아웃 성공 응답",
                                            value = LOGOUT_MEMBER_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "로그인 하지 않은 유저의 요청입니다.",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @GetMapping("/logout")
    public ResponseEntity<BaseResponse<String>> logoutMember() {
        ResponseCookie deleteAccessToken = ResponseCookie.from(ACCESS_TOKEN, "")
                .httpOnly(true)
                .secure(true)
//                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteAccessToken.toString())
                .body(BaseResponse.of("로그아웃 성공했습니다.", HttpStatus.OK));
    }

    @Operation(
            summary = "이메일 중복 체크",
            description = "새로 회원가입할 이메일이 중복이 있는지 확인합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "새로 가입할 이메일",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemberEmailCheckRequestDto.class),
                            examples = @ExampleObject(
                                    name = "이메일 중복 체크 예시",
                                    value = CHECK_EMAIL_MEMBER_REQUEST
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "이메일 중복 체크",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MemberEmailCheckResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "이메일 중복이 없음을 응답",
                                            value = CHECK_EMAIL_MEMBER_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "이미 가입한 이메일입니다.",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping("/exists/email")
    public ResponseEntity<BaseResponse<MemberEmailCheckResponseDto>> checkEmail(@Valid @RequestBody MemberEmailCheckRequestDto dto) {
        MemberEmailCheckResponseDto memberEmailCheckResponseDto = memberService.checkMemberEmail(dto.getEmail());
        return ResponseEntity.status(200).body(BaseResponse.of(memberEmailCheckResponseDto, HttpStatus.OK));
    }

    @Operation(
            summary = "채널 이름 중복 체크",
            description = "새로 회원가입할 채널 이름이 중복이 있는지 확인합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "새로 가입할 채널 이름",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemberChannelNameCheckRequestDto.class),
                            examples = @ExampleObject(
                                    name = "채널 이름 중복 체크 예시",
                                    value = CHECK_CHANNEL_NAME_MEMBER_REQUEST
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "채널 이름 중복 체크",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MemberChannelNameCheckResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "채널 이름이 없음을 응답",
                                            value = CHECK_CHANNEL_NAME_MEMBER_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "이미 가입한 채널 이름입니다.",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping("/exists/channel")
    public ResponseEntity<BaseResponse<MemberChannelNameCheckResponseDto>> checkChannelName(@Valid @RequestBody MemberChannelNameCheckRequestDto dto) {
        MemberChannelNameCheckResponseDto memberChannelNameCheckResponseDto = memberService.checkMemberChannelName(dto.getChannelName());
        return ResponseEntity.status(200).body(BaseResponse.of(memberChannelNameCheckResponseDto, HttpStatus.OK));
    }

    @Operation(
            summary = "멤버 정보 수정",
            description = "기존 멤버의 정보를 수정합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "멤버의 채널 이름 및 설명을 수정합니다.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemberUpdateChannelRequestDto.class),
                            examples = @ExampleObject(
                                    name = "채널 정보 수정 예시",
                                    value = UPDATE_MEMBER_INFO_REQUEST
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "채널 정보 수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(
                                            name = "채널 정보 수정이 완료되었음을 응답",
                                            value = UPDATE_MEMBER_INFO_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "채널 이름이 중복됩니다. 다른 채널 이름으로 변경해주세요",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PatchMapping("/update")
    public ResponseEntity<BaseResponse<String>> updateNameMember(@Valid @RequestBody MemberUpdateChannelRequestDto dto,
                                           @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        memberService.updateMemberName(memberDetailsDto, dto);
        return ResponseEntity.ok(BaseResponse.of("회원 이름 변경 성공", HttpStatus.OK));
    }

    @Operation(
            summary = "멤버 정보 조회",
            description = "기존 멤버의 정보를 조회합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "없음",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(),
                            examples = @ExampleObject(
                                    name = "채널 정보 조회 예시",
                                    value = ""
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "채널 정보 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MemberInfoResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "채널 정보 조회가 완료되었음을 응답",
                                            value = READ_MEMBER_INFO_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "로그인 해주세요",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @GetMapping("/info")
    public ResponseEntity<BaseResponse<MemberInfoResponseDto>> readMemberInfo(@AuthenticationPrincipal MemberDetailsDto dto) {
        MemberInfoResponseDto memberInfoResponseDto = memberService.readMemberInfo(dto);
        return ResponseEntity.ok(BaseResponse.of(memberInfoResponseDto, HttpStatus.OK));
    }

    @Operation(
            summary = "멤버 삭제",
            description = "기존 멤버를 삭제합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "없음",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(),
                            examples = @ExampleObject(
                                    name = "멤버 삭제",
                                    value = ""
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "멤버 삭제 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(
                                            name = "멤버 삭제가 완료되었음을 응답",
                                            value = DELETE_MEMBER_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "로그인 해주세요",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @DeleteMapping
    public ResponseEntity<BaseResponse<String>> deleteMember(@AuthenticationPrincipal MemberDetailsDto dto) {
        memberService.deleteMember(dto);
        ResponseCookie deleteAccessToken = ResponseCookie.from(ACCESS_TOKEN, "")
                .httpOnly(true)
                .secure(true)
//                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteAccessToken.toString())
                .body(BaseResponse.of("삭제 완료 되었습니다. 실제 데이터 삭제까지는 하루정도 소요됩니다.", HttpStatus.OK));
    }
    @Operation(
            summary = "채널 정보 조회",
            description = "채널 고유 ID를 통해 특정 채널의 정보를 조회합니다.",
            parameters = {
                    @Parameter(
                            name = "channelIdx",
                            description = "조회할 채널의 고유 ID",
                            required = true,
                            example = "1",
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "채널 정보 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MemberInfoResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "채널 정보 조회 완료",
                                            value = READ_MEMBER_INFO_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "존재하지 않는 채널입니다.",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "채널을 찾을 수 없습니다.",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @GetMapping("/info/{channelName}")
    public ResponseEntity<BaseResponse<MemberInfoResponseDto>> getChannelInfo(@PathVariable String channelName) {
        MemberInfoResponseDto channelInfo = memberService.getChannelInfoByChannelName(channelName);
        return ResponseEntity.ok(BaseResponse.of(channelInfo, HttpStatus.OK));
    }

    @GetMapping("/token")
    public ResponseEntity<BaseResponse<String>> getToken(@AuthenticationPrincipal MemberDetailsDto dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + dto.getJwt());

        return ResponseEntity.ok().headers(headers).body(BaseResponse.of("데이터 전달 성공", HttpStatus.OK));
    }
}
