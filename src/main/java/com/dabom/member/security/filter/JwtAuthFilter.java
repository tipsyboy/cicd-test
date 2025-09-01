package com.dabom.member.security.filter;

import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.member.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.dabom.member.contants.JWTConstants.*;

public class JwtAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();

        String jwt = findDabomJWT(cookies);
        exceptionHandler(jwt);

        filterChain.doFilter(request, response);
    }

    private void exceptionHandler(String jwt) {
        try {
            haveTokenLogic(jwt);
        } catch (ExpiredJwtException e) {
            // 만료 토큰 302 Redirect로 RefreshToken
        } catch (SecurityException | MalformedJwtException e) {
            // 잘못된 토큰 403 Error 내려주기
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 토큰 403 Error 내려주기
        } catch (IllegalArgumentException e) {
            // 빈 토큰 403 Error 내려주기
        }
    }

    private String findDabomJWT(Cookie[] cookies) {
        String jwt = null;
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(Objects.equals(cookie.getName(), ACCESS_TOKEN)) {
                    jwt = cookie.getValue();
                }
                break;
            }
        }
        return jwt;
    }

    private void haveTokenLogic(String jwt) {
        if(jwt != null) {
            Claims claims = JwtUtils.getClaims(jwt);
            haveDabomTokenLogic(claims, jwt);
        }
    }

    private void haveDabomTokenLogic(Claims claims, String jwt) {
        if(claims != null) {
            MemberDetailsDto dto = createDetailsFromToken(claims, jwt);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    dto,
                    null,
                    List.of(new SimpleGrantedAuthority(dto.getMemberRole().name()))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private MemberDetailsDto createDetailsFromToken(Claims claims, String jwt) {
        Integer idx = Integer.parseInt(JwtUtils.getValue(claims, TOKEN_IDX));
        String name = JwtUtils.getValue(claims, TOKEN_NAME);
        String role = JwtUtils.getValue(claims, TOKEN_USER_ROLE);
        return MemberDetailsDto.createFromToken(idx, name, role, jwt);
    }
}
