package com.dabom.config.interceptor;

import com.dabom.member.security.dto.MemberDetailsDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import com.dabom.member.utils.JwtUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.dabom.member.contants.JWTConstants.*;
import static com.dabom.member.contants.JWTConstants.TOKEN_USER_ROLE;


// 웹소켓 처음 실행할때 실행

@Component
public class JwtHandShakeInterceptor implements HandshakeInterceptor {


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletServerHttpRequest) {
            HttpServletRequest httpReq = servletServerHttpRequest.getServletRequest();

            Cookie[] cookies = httpReq.getCookies();

            String jwt = findDabomJWT(cookies);
            exceptionHandler(jwt, attributes);
        }
        return true;
    }


    private void exceptionHandler(String jwt, Map<String, Object> attributes) {
        try {
            haveTokenLogic(jwt, attributes);
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
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Objects.equals(cookie.getName(), ACCESS_TOKEN)) {
                    jwt = cookie.getValue();
                }
                break;
            }
        }
        return jwt;
    }

    private void haveTokenLogic(String jwt, Map<String, Object> attributes) {
        if (jwt != null) {
            Claims claims = JwtUtils.getClaims(jwt);
            haveDabomTokenLogic(claims, attributes, jwt);
        }
    }

    private void haveDabomTokenLogic(Claims claims, Map<String, Object> attributes, String jwt) {
        if (claims != null) {
            MemberDetailsDto dto = createDetailsFromToken(claims, jwt);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    dto,
                    null,
                    List.of(new SimpleGrantedAuthority(dto.getMemberRole().name()))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            attributes.put("auth", authentication);
        }
    }

    private MemberDetailsDto createDetailsFromToken(Claims claims, String jwt) {
        Integer idx = Integer.parseInt(JwtUtils.getValue(claims, TOKEN_IDX));
        String name = JwtUtils.getValue(claims, TOKEN_NAME);
        String role = JwtUtils.getValue(claims, TOKEN_USER_ROLE);
        return MemberDetailsDto.createFromToken(idx, name, role, jwt);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
