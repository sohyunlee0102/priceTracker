package com.example.priceTracker.controller;

import com.example.priceTracker.apiPayload.ApiResponse;
import com.example.priceTracker.dto.userDto.UserRequestDTO;
import com.example.priceTracker.dto.userDto.UserResponseDTO;
import com.example.priceTracker.service.authService.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<UserResponseDTO.LoginResponseDto> login(@Valid @RequestBody UserRequestDTO.loginDto request) {
        return ApiResponse.onSuccess(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<UserResponseDTO.LoginResponseDto> refreshToken(@Valid @RequestBody UserRequestDTO.RefreshTokenDTO request) {
        return ApiResponse.onSuccess(authService.refreshAccessToken(request));
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(@AuthenticationPrincipal UserDetails userDetails,
                                      @RequestHeader("Authorization") String token) {
        String accessToken = token.substring(7);
        authService.logout(userDetails.getUsername(), accessToken);
        return ApiResponse.onSuccess("로그아웃 되었습니다.");
    }

}
