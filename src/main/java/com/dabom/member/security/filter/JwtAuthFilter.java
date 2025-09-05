package com.dabom.member.security.filter;

import com.dabom.common.BaseResponse;
import com.dabom.member.model.entity.MemberRole;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.member.security.dto.Token;
import com.dabom.member.utils.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.dabom.member.contants.JWTConstants.*;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String uri = request.getRequestURL().toString();
        Token dabomJWT = findDabomJWT(cookies);
        if(!exceptionHandler(dabomJWT, response, uri)) {
            filterChain.doFilter(request, response);
        }
    }

    private boolean exceptionHandler(Token jwt, HttpServletResponse response, String uri) throws IOException {
        try {
            haveTokenLogic(jwt.aT());
            return false;
        } catch (ExpiredJwtException e) {
            haveRefreshTokenLogic(jwt.rT(), response, uri);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            // 잘못된 토큰 403 Error 내려주기
            return true;
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 토큰 403 Error 내려주기
            return true;
        } catch (IllegalArgumentException e) {
            // 빈 토큰 403 Error 내려주기
            return true;
        }
    }

    private Token findDabomJWT(Cookie[] cookies) {
        String aT = null;
        String rT = null;
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(Objects.equals(cookie.getName(), ACCESS_TOKEN)) {
                    aT = cookie.getValue();
                }
                if(Objects.equals(cookie.getName(), REFRESH_TOKEN)) {
                    rT = cookie.getValue();
                }
            }
        }
        return Token.toDto(aT, rT);
    }

    private void haveTokenLogic(String jwt) {
        if(jwt != null) {
            Claims claims = JwtUtils.getClaims(jwt);
            haveDabomTokenLogic(claims);
        }
    }

    private void haveDabomTokenLogic(Claims claims) {
        if(claims != null) {
            MemberDetailsDto dto = createDetailsFromToken(claims);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    dto,
                    null,
                    List.of(new SimpleGrantedAuthority(dto.getMemberRole().name()))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private MemberDetailsDto createDetailsFromToken(Claims claims) {
        Integer idx = Integer.parseInt(JwtUtils.getValue(claims, TOKEN_IDX));
        String name = JwtUtils.getValue(claims, TOKEN_NAME);
        String role = JwtUtils.getValue(claims, TOKEN_USER_ROLE);
        return MemberDetailsDto.createFromToken(idx, name, role);
    }

    private void haveRefreshTokenLogic(String jwt, HttpServletResponse response, String uri) throws IOException {
        if(jwt != null) {
            Claims claims = JwtUtils.getClaims(jwt);
            Token token = reCreateToken(claims);

            redirectToken(response, token, uri);
        }
    }

    private Token reCreateToken(Claims claims) {
        Integer idx = Integer.parseInt(JwtUtils.getValue(claims, TOKEN_IDX));
        String name = JwtUtils.getValue(claims, TOKEN_NAME);
        String role = JwtUtils.getValue(claims, TOKEN_USER_ROLE);

        String aT = JwtUtils.generateLoginToken(idx, name, MemberRole.valueOf(role));
        String rT = JwtUtils.generateRefreshToken(idx, name, MemberRole.valueOf(role));

        return Token.toDto(aT, rT);
    }

    private void redirectToken(HttpServletResponse response, Token token, String uri) throws IOException {
        createCookie(ACCESS_TOKEN, token.aT(), response);
        createCookie(REFRESH_TOKEN, token.rT(), response);

        String jsonResponse = createResponse(response, uri);

        response.getWriter().write(jsonResponse);
    }

    private String createResponse(HttpServletResponse response, String uri) throws JsonProcessingException {
        response.setStatus(HttpStatus.FOUND.value());
        response.setHeader("Location", uri);
        response.setContentType("application/json");
        BaseResponse<String> res = BaseResponse.of("새로운 토큰이 발급되었습니다.", HttpStatus.OK);
        return objectMapper.writeValueAsString(res);
    }

    private void createCookie(String tokenName, String token, HttpServletResponse response) {
        Cookie newRefreshTokenCookie = new Cookie(tokenName, token);
        newRefreshTokenCookie.setPath("/");
        newRefreshTokenCookie.setHttpOnly(true);
        if(tokenName.equals(ACCESS_TOKEN)) {
            newRefreshTokenCookie.setMaxAge(ACCESS_TOKEN_EXP);
        } else {
            newRefreshTokenCookie.setMaxAge(REFRESH_TOKEN_EXP);
        }
        // 필요에 따라 secure, maxAge 설정 추가
        response.addCookie(newRefreshTokenCookie);
    }
}
