package com.example.priceTracker.config.security;

import com.example.priceTracker.apiPayload.code.status.ErrorStatus;
import com.example.priceTracker.apiPayload.exception.handler.AuthHandler;
import com.example.priceTracker.apiPayload.exception.handler.UserHandler;
import com.example.priceTracker.domain.enums.UserStatus;
import com.example.priceTracker.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        com.example.priceTracker.domain.user.User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    return new AuthHandler(ErrorStatus.AUTHENTICATION_FAILED);
                });

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new UserHandler(ErrorStatus.INACTIVE_USER);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }

}
