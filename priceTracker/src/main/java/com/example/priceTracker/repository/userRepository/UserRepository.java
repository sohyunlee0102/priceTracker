package com.example.priceTracker.repository.userRepository;

import com.example.priceTracker.domain.enums.UserStatus;
import com.example.priceTracker.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByStatusAndInactiveDateBefore(UserStatus status, LocalDate inactiveDate);
}
