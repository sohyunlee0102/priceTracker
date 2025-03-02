package com.example.priceTracker.config.jwt;

import com.example.priceTracker.apiPayload.code.status.ErrorStatus;
import com.example.priceTracker.apiPayload.exception.handler.AuthHandler;
import com.example.priceTracker.apiPayload.exception.handler.UserHandler;
import com.example.priceTracker.domain.user.User;
import com.example.priceTracker.service.userService.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 30; // 30분
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7일

    @Value("${jwt.secret.key}")
    private String secretKey;

    private static SecretKey SECRET_KEY;
    @PostConstruct
    public void init() {
        SECRET_KEY = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public static String generateAccessToken(String email, UserService userService) {

        User user = userService.findUserByEmail(email);

        return Jwts.builder()
                .setSubject(email)
                .setId(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateRefreshToken(String email, UserService userService) {

        User user = userService.findUserByEmail(email);

        return Jwts.builder()
                .setSubject(email)
                .setId(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new AuthHandler(ErrorStatus.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new AuthHandler(ErrorStatus.INVALID_TOKEN);
        }
    }

    public static Long extractMemberId(String authorization) {
        String accessToken = authorization.substring(7);
        Claims claims = validateToken(accessToken);
        String memberId = claims.getId();
        if (memberId == null || memberId.isEmpty()) {
            throw new AuthHandler(ErrorStatus.INVALID_TOKEN);
        }
        return Long.parseLong(memberId);
    }

    // 유저 이메일 추출 (온보딩 사용 - 임시)
    public static String extractUserEmail(String token) {
        String accessToken = token.substring(7);
        Claims claims = validateToken(accessToken);
        String email = claims.getSubject();
        if (email == null || email.isEmpty()) {
            throw new AuthHandler(ErrorStatus.INVALID_TOKEN);  // 커스텀 예외 처리
        }
        return email;
    }

    public static long getExpiration(String token) {
        try {
            Claims claims = validateToken(token);
            System.out.println(claims);
            Date expiration = claims.getExpiration();
            System.out.println(expiration);
            return expiration.getTime();
        } catch (ExpiredJwtException e) {
            throw new AuthHandler(ErrorStatus.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new AuthHandler(ErrorStatus.INVALID_TOKEN);
        }
    }

    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String message) {
            super(message);
        }
    }

    public static class TokenExpiredException extends RuntimeException {
        public TokenExpiredException(String message) {
            super(message);
        }
    }

}