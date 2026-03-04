package com.revnu.backend.service;

import com.revnu.backend.dto.AuthResponse;
import com.revnu.backend.dto.LoginRequest;
import com.revnu.backend.dto.RegisterRequest;
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

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already taken");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Single-Tenant Logic: Using your RoleType Enum
        if (userRepository.count() == 0) {
            user.setRole(RoleType.OWNER); // First user is Boss
            user.setStatus(UserStatus.ACTIVE);
        } else {
            user.setRole(RoleType.STAFF); // Others are Staff
            user.setStatus(UserStatus.PENDING);
        }

        User saved = userRepository.save(user);
        return new AuthResponse("Registration successful", saved.getEmail(), saved.getFullName(), saved.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (user.getStatus() == UserStatus.PENDING) {
            throw new RuntimeException("Your account is pending approval by the owner");
        }

        return new AuthResponse("Login successful", user.getEmail(), user.getFullName(), user.getRole());
    }
}