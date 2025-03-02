package com.example.priceTracker.dto.userDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class UserRequestDTO {

    @Getter
    @Setter
    public static class JoinDto {

        @NotBlank
        @Email
        String email;
        @NotBlank
        String password;
        @NotBlank
        String nickname;

    }

    @Getter
    @Setter
    public static class loginDto {

        @NotBlank
        @Email
        String email;
        @NotBlank
        String password;

    }

    @Getter
    @Setter
    public static class RefreshTokenDTO {

        @NotNull
        String refreshToken;
        @NotBlank
        @Email
        String email;
    }

}
