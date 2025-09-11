package com.dabom.member.security.handler;

import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.member.utils.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.dabom.member.contants.JWTConstants.ACCESS_TOKEN;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        MemberDetailsDto dto = (MemberDetailsDto) authentication.getPrincipal();

        String aT = JwtUtils.generateLoginToken(dto.getIdx(), dto.getEmail(), dto.getMemberRole());
        String rT = JwtUtils.generateRefreshToken(dto.getIdx(), dto.getEmail(), dto.getMemberRole());

        if (aT != null && rT != null) {
            Cookie access = new Cookie(ACCESS_TOKEN, aT);
            Cookie refresh = new Cookie(ACCESS_TOKEN, rT);

            setCookieSetting(access);
            setCookieSetting(refresh);
            response.addCookie(access);
            response.addCookie(refresh);

            response.setContentType("text/html");
            response.getWriter().write(
                    "<script>" +
                            "window.opener.postMessage('true', '*');" +
                            "window.close();" +
                            "</script>"
            );
        }
    }

    private void setCookieSetting(Cookie cookie) {
        cookie.setHttpOnly(true);
        cookie.setPath("/");
    }
}
