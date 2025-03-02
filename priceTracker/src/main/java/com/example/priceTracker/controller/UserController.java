package com.example.priceTracker.controller;

import com.example.priceTracker.apiPayload.ApiResponse;
import com.example.priceTracker.domain.user.User;
import com.example.priceTracker.dto.userDto.UserRequestDTO;
import com.example.priceTracker.dto.userDto.UserResponseDTO;
import com.example.priceTracker.service.userService.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<UserResponseDTO.UserDto> getUsersByEmail(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.onSuccess(userService.getUserByEmail(userDetails.getUsername()));
    }

    @PostMapping
    public ApiResponse<UserResponseDTO.JoinResultDTO> join(@Valid @RequestBody UserRequestDTO.JoinDto request) {
        return ApiResponse.onSuccess(userService.joinUser(request));
    }

    @DeleteMapping
    public ApiResponse<String> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteUser(userDetails.getUsername());
        return ApiResponse.onSuccess("User deleted.");
    }

}
