package com.example.priceTracker.service.userService;

import com.example.priceTracker.apiPayload.ApiResponse;
import com.example.priceTracker.apiPayload.code.status.ErrorStatus;
import com.example.priceTracker.apiPayload.exception.handler.UserHandler;
import com.example.priceTracker.domain.enums.Role;
import com.example.priceTracker.domain.enums.UserStatus;
import com.example.priceTracker.domain.listener.ListenerUtil;
import com.example.priceTracker.domain.user.User;
import com.example.priceTracker.dto.userDto.UserRequestDTO;
import com.example.priceTracker.dto.userDto.UserResponseDTO;
import com.example.priceTracker.repository.userRepository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Transactional
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
    }

    @Transactional
    public UserResponseDTO.UserDto getUserByEmail(String email) {
        User user = findUserByEmail(email);
        return UserResponseDTO.UserDto.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public UserResponseDTO.JoinResultDTO joinUser(UserRequestDTO.JoinDto request) {
        ListenerUtil.disableListener();

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            if (user.getStatus() == UserStatus.INACTIVE) {
                user.setStatus(UserStatus.ACTIVE);
                user.setInactiveDate(null);
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setUsername(request.getNickname());
                userRepository.save(user);
                ListenerUtil.enableListener();
                return new UserResponseDTO.JoinResultDTO(user.getId(), LocalDateTime.now());
            } else {
                throw new UserHandler(ErrorStatus.EMAIL_ALREADY_EXISTS);
            }
        }

        User newUser = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .username(request.getNickname())
                .role(Role.USER)
                .build();

        newUser.encodePassword(passwordEncoder.encode(newUser.getPassword()));

        userRepository.save(newUser);

        ListenerUtil.enableListener();

        return new UserResponseDTO.JoinResultDTO(newUser.getId(), LocalDateTime.now());
    }

    @Transactional
    public void deleteUser(String email) {
        User user = findUserByEmail(email);

        stringRedisTemplate.delete(user.getEmail());

        user.setStatus(UserStatus.INACTIVE);
        user.setInactiveDate(LocalDate.now());
        userRepository.save(user);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deleteInactiveUsers() {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<User> usersToDelete = userRepository.findByStatusAndInactiveDateBefore(UserStatus.INACTIVE, thirtyDaysAgo);

        userRepository.deleteAll(usersToDelete);
    }

}
