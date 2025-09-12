package com.dabom.member.service;

import com.dabom.member.security.config.CookieProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieService {
    private final CookieProperties cookieProperties;

    public ResponseCookie createJwtCookie(String name, String value, Long maxAgeSecond) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .domain(cookieProperties.getDomain())
                .path("/")
                .sameSite(cookieProperties.getSameSite())
                .maxAge(maxAgeSecond)
                .build();
    }

    public void addCookieToResponse(ResponseCookie cookie, HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
