package com.example.priceTracker.service.authService;

import com.example.priceTracker.apiPayload.code.status.ErrorStatus;
import com.example.priceTracker.apiPayload.exception.handler.AuthHandler;
import com.example.priceTracker.config.jwt.JwtUtil;
import com.example.priceTracker.dto.userDto.UserRequestDTO;
import com.example.priceTracker.dto.userDto.UserResponseDTO;
import com.example.priceTracker.service.userService.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ValueOperations<String, String> redisOps;
    private final UserService userService;

    @Transactional
    public UserResponseDTO.LoginResponseDto login(UserRequestDTO.loginDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            String accessToken = jwtUtil.generateAccessToken(request.getEmail(), userService);
            String refreshToken = jwtUtil.generateRefreshToken(request.getEmail(), userService);

            redisOps.set(request.getEmail(), refreshToken, 7, TimeUnit.DAYS);

            return UserResponseDTO.LoginResponseDto.builder()
                    .email(request.getEmail())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (AuthenticationException e) {
            throw new AuthHandler(ErrorStatus.AUTHENTICATION_FAILED);
        }
    }

    @Transactional
    public UserResponseDTO.LoginResponseDto refreshAccessToken(UserRequestDTO.RefreshTokenDTO request) {

        String storedRefreshToken = redisOps.get(request.getEmail());

        if (storedRefreshToken == null || !storedRefreshToken.equals(request.getRefreshToken())) {
            throw new AuthHandler(ErrorStatus.INVALID_TOKEN);
        }

        String newAccessToken = jwtUtil.generateAccessToken(request.getEmail(), userService);

        return UserResponseDTO.LoginResponseDto.builder()
                .email(request.getEmail())
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Transactional
    public void logout(String email, String accessToken) {
        System.out.println("email: " + email);
        System.out.println("accessToken: " + accessToken);
        redisOps.getOperations().delete(email);
        System.out.println("Deleted");
        long expiration = jwtUtil.getExpiration(accessToken);
        System.out.println("expiration: " + expiration);
        redisOps.getOperations().opsForValue().set("BLACKLIST" + accessToken, "logout", expiration, TimeUnit.SECONDS);
    }

}
