package com.revnu.backend.service;

import com.revnu.backend.model.User;
import com.revnu.backend.repository.UserRepository;
import com.revnu.backend.model.RoleType;
import com.revnu.backend.model.UserStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already taken");
        }

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        // Single-Tenant Logic: Using your RoleType Enum
        if (userRepository.count() == 0) {
            user.setRole(RoleType.OWNER); // First user is Boss
            user.setStatus(UserStatus.ACTIVE);
        } else {
            user.setRole(RoleType.STAFF); // Others are Staff
            user.setStatus(UserStatus.PENDING);
        }

        return userRepository.save(user);
    }
}