package com.revnu.backend.service;

import com.revnu.backend.dto.AuthResponse;
import com.revnu.backend.dto.LoginRequest;
import com.revnu.backend.dto.RegisterRequest;
import com.revnu.backend.model.User;
import com.revnu.backend.repository.UserRepository;
import com.revnu.backend.model.RoleType;
import com.revnu.backend.model.UserStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already taken");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        if (userRepository.count() == 0) {
            user.setRole(RoleType.OWNER); 
            user.setStatus(UserStatus.ACTIVE);
        } else {
            user.setRole(RoleType.STAFF);
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

    public AuthResponse authenticateWithGoogleOAuth2User(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name  = oAuth2User.getAttribute("name");
        String googleId = oAuth2User.getAttribute("sub"); 

        if (email == null) {
            throw new IllegalArgumentException("Google account did not provide an email address");
        }

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName(name != null ? name : email);
            newUser.setPasswordHash(""); 
            newUser.setOauthId(googleId);

            if (userRepository.count() == 0) {
                newUser.setRole(RoleType.OWNER);
            } else {
                newUser.setRole(RoleType.STAFF);
            }
            newUser.setStatus(UserStatus.ACTIVE);
            return userRepository.save(newUser);
        });

       
        if (user.getOauthId() == null) {
            throw new IllegalArgumentException("existing_account_requires_link");
        }

        if (user.getStatus() == UserStatus.PENDING) {
            throw new IllegalArgumentException("Your account is pending approval by the owner");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse("Google login successful", user.getEmail(), user.getFullName(), user.getRole(), token);
    }

    public AuthResponse linkGoogleAccount(com.revnu.backend.dto.LinkGoogleRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("No account found for this email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Incorrect password");
        }

        user.setOauthId(request.getGoogleId());
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse("Google account linked successfully", user.getEmail(), user.getFullName(), user.getRole(), token);
    }
}