package com.revnu.backend.facade;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.revnu.backend.adapter.GoogleUserAdapter;
import com.revnu.backend.dto.AuthResponse;
import com.revnu.backend.dto.LinkGoogleRequest;
import com.revnu.backend.dto.LoginRequest;
import com.revnu.backend.dto.RegisterRequest;
import com.revnu.backend.model.RoleType;
import com.revnu.backend.model.User;
import com.revnu.backend.model.UserStatus;
import com.revnu.backend.repository.UserRepository;
import com.revnu.backend.service.JwtService;
import com.revnu.backend.strategy.RoleAssignmentService;
import com.revnu.backend.validator.RegistrationValidationPipeline;

@Service
public class AuthFacade {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RegistrationValidationPipeline validationPipeline;
    private final RoleAssignmentService roleAssignmentService;

    public AuthFacade(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      JwtService jwtService,
                      RegistrationValidationPipeline validationPipeline,
                      RoleAssignmentService roleAssignmentService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.validationPipeline = validationPipeline;
        this.roleAssignmentService = roleAssignmentService;
    }

    
    public AuthResponse register(RegisterRequest request) {
        
        validationPipeline.validateRegistration(request);

        RoleType assignedRole = roleAssignmentService.assignRole();
        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(assignedRole)
                .status(assignedRole.name().equals("OWNER") ? UserStatus.ACTIVE : UserStatus.PENDING)
                .build();

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved.getEmail());
        return new AuthResponse("Registration successful", saved.getEmail(), saved.getFullName(), saved.getRole(), token);
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

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse("Login successful", user.getEmail(), user.getFullName(), user.getRole(), token);
    }

   
    public AuthResponse authenticateWithGoogle(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = GoogleUserAdapter.adaptGoogleUser(oAuth2User)
                    .role(roleAssignmentService.assignRole())
                    .status(UserStatus.ACTIVE)
                    .build();
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

    public AuthResponse linkGoogleAccount(LinkGoogleRequest request) {
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
