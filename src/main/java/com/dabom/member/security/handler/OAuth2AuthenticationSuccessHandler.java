package com.dabom.member.security.handler;

import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.member.service.CookieService;
import com.dabom.member.utils.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.dabom.member.contants.JWTConstants.*;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final CookieService cookieService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        MemberDetailsDto dto = (MemberDetailsDto) authentication.getPrincipal();

        String aT = JwtUtils.generateLoginToken(dto.getIdx(), dto.getEmail(), dto.getMemberRole());
        String rT = JwtUtils.generateRefreshToken(dto.getIdx(), dto.getEmail(), dto.getMemberRole());

        if (aT != null && rT != null) {
            ResponseCookie aTCookie = cookieService.createJwtCookie(ACCESS_TOKEN, aT, (long) ACCESS_TOKEN_EXP);
            ResponseCookie rTCookie = cookieService.createJwtCookie(REFRESH_TOKEN, rT, (long) REFRESH_TOKEN_EXP);

            cookieService.addCookieToResponse(aTCookie, response);
            cookieService.addCookieToResponse(rTCookie, response);

            response.setContentType("text/html");
            response.getWriter().write(
                    "<script>" +
                            "window.opener.postMessage('true', '*');" +
                            "window.close();" +
                            "</script>"
            );
        }
    }
}
