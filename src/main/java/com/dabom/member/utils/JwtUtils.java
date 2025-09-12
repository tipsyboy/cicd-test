package com.dabom.member.utils;

import com.dabom.member.model.entity.MemberRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.dabom.member.contants.JWTConstants.*;

public class JwtUtils {
    private static final String SECRET = "abcdeffghijklmnopqrstuvwxyz0123456";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    private static final Long EXP = 1000 * 60 * 30L; // 30분
    private static final Long REFRESH_EXP = 1000 * 60 * 60 * 12L; // 12시간

    public static String generateLoginToken(Integer idx, String name, MemberRole role) {
        Map<String, String> claims =  new HashMap<>();
        claims.put(TOKEN_IDX, ""+idx);
        claims.put(TOKEN_NAME, name);
        claims.put(TOKEN_USER_ROLE, role.name());

        return Jwts.builder()
//                .setSubject(name)
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXP))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateRefreshToken(Integer idx, String name, MemberRole role) {
        Map<String, String> claims =  new HashMap<>();
        claims.put(TOKEN_IDX, ""+idx);
        claims.put(TOKEN_NAME, name);
        claims.put(TOKEN_USER_ROLE, role.name());

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXP))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }


    public static String getValue(Claims claims, String key) {
        return (String) claims.get(key);
    }

    public static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
