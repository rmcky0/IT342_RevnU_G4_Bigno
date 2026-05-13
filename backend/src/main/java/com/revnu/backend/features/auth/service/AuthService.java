package com.revnu.backend.features.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.revnu.backend.features.auth.adapter.GoogleUserAdapter;
import com.revnu.backend.features.auth.dto.AuthResponse;
import com.revnu.backend.features.auth.dto.LinkGoogleRequest;
import com.revnu.backend.features.auth.dto.LoginRequest;
import com.revnu.backend.features.auth.dto.RegisterRequest;
import com.revnu.backend.features.auth.model.AccountStatus;
import com.revnu.backend.features.auth.model.RoleType;
import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.auth.validator.RegistrationValidationPipeline;
import com.revnu.backend.features.notifications.service.NotificationService;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;
import com.revnu.backend.shared.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final RegistrationValidationPipeline validationPipeline;
    private final RestaurantRepository restaurantRepository;
    private final NotificationService notificationService;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            TokenBlacklistService tokenBlacklistService,
            RegistrationValidationPipeline validationPipeline,
            RestaurantRepository restaurantRepository,
            NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.validationPipeline = validationPipeline;
        this.restaurantRepository = restaurantRepository;
        this.notificationService = notificationService;
    }

    public AuthResponse register(RegisterRequest request) {
        validationPipeline.validateRegistration(request);

        User user = User.builder()
                .email(request.email())
                .fullname(request.fullname())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(RoleType.TENANT)
                .status(AccountStatus.ACTIVE)
                .provider("local")
                .build();

        User saved = userRepository.save(user);
        notificationService.notifyAdminsUserRegistered(saved);
        String token = jwtService.generateToken(saved.getEmail());

        return new AuthResponse(
                "Registration successful",
                saved.getEmail(),
                saved.getFullname(),
                saved.getRole(),
                saved.getStatus(),
                saved.getProvider(),
                false,
                token
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());
        boolean hasRestaurant = restaurantRepository.existsByOwner(user);

        return new AuthResponse(
                "Login successful",
                user.getEmail(),
                user.getFullname(),
                user.getRole(),
                user.getStatus(),
                user.getProvider(),
                hasRestaurant,
                token
        );
    }

    public AuthResponse authenticateWithGoogle(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            user = GoogleUserAdapter.adaptGoogleUser(oAuth2User)
                    .role(RoleType.TENANT)
                    .status(AccountStatus.ACTIVE)
                    .provider("google")
                    .build();
            user = userRepository.save(user);
            notificationService.notifyAdminsUserRegistered(user);
        }

        if (user.getOauthId() == null) {
            throw new IllegalArgumentException("existing_account_requires_link");
        }

        String token = jwtService.generateToken(user.getEmail());
        boolean hasRestaurant = restaurantRepository.existsByOwner(user);

        return new AuthResponse(
                "Google login successful",
                user.getEmail(),
                user.getFullname(),
                user.getRole(),
                user.getStatus(),
                user.getProvider(),
                hasRestaurant,
                token
        );
    }

    public AuthResponse linkGoogleAccount(LinkGoogleRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("No account found for this email"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Incorrect password");
        }

        user.setOauthId(request.googleId());
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        boolean hasRestaurant = restaurantRepository.existsByOwner(user);

        return new AuthResponse(
                "Google account linked successfully",
                user.getEmail(),
                user.getFullname(),
                user.getRole(),
                user.getStatus(),
                user.getProvider(),
                hasRestaurant,
                token
        );
    }

    public AuthResponse logout(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token is required for logout");
        }

        String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        tokenBlacklistService.blacklistToken(cleanToken);

        return new AuthResponse(
                "Logout successful",
                null, null, null, null, null, false, null
        );
    }

    public AuthResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean hasRestaurant = restaurantRepository.existsByOwner(user);

        return new AuthResponse(
                "Current user",
                user.getEmail(),
                user.getFullname(),
                user.getRole(),
                user.getStatus(),
                user.getProvider(),
                hasRestaurant,
                null
        );
    }
}
