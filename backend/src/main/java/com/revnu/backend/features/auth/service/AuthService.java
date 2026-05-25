package com.revnu.backend.features.auth.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.revnu.backend.features.auth.adapter.GoogleUserAdapter;
import com.revnu.backend.features.auth.dto.AuthResponse;
import com.revnu.backend.features.auth.dto.LinkGoogleRequest;
import com.revnu.backend.features.auth.dto.LoginRequest;
import com.revnu.backend.features.auth.dto.RegisterRequest;
import com.revnu.backend.features.auth.model.AccountStatus;
import com.revnu.backend.features.auth.model.RoleType;
import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.observer.AuthEventListener;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.auth.validator.RegistrationValidationPipeline;
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
    private final List<AuthEventListener> authEventListeners;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            TokenBlacklistService tokenBlacklistService,
            RegistrationValidationPipeline validationPipeline,
            RestaurantRepository restaurantRepository,
            List<AuthEventListener> authEventListeners) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.validationPipeline = validationPipeline;
        this.restaurantRepository = restaurantRepository;
        this.authEventListeners = authEventListeners;
    }

    public AuthResponse register(RegisterRequest request) {
        validationPipeline.validateRegistration(request);

        User user = User.builder()
                .email(request.email())
                .fullname(request.fullname())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(RoleType.RESTAURATEUR)
                .status(AccountStatus.ACTIVE)
                .provider("local")
                .build();

        User saved = userRepository.save(user);
        authEventListeners.forEach(l -> l.onUserRegistered(saved));
        String token = jwtService.generateToken(saved.getEmail());
        String refreshToken = jwtService.generateRefreshToken(saved.getEmail());

        return new AuthResponse(
                "Registration successful",
                saved.getEmail(),
                saved.getFullname(),
                saved.getRole(),
                saved.getStatus(),
                saved.getProvider(),
                false,
                token,
                refreshToken
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (user.getStatus() == AccountStatus.SUSPENDED) {
            throw new SecurityException("Account is suspended.");
        }

        if ("google".equals(user.getProvider()) &&
                (user.getPasswordHash() == null || user.getPasswordHash().isBlank())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "This account uses Google Sign-In. Please use the 'Continue with Google' button to sign in.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        boolean hasRestaurant = restaurantRepository.existsByOwner(user);

        return new AuthResponse(
                "Login successful",
                user.getEmail(),
                user.getFullname(),
                user.getRole(),
                user.getStatus(),
                user.getProvider(),
                hasRestaurant,
                token,
                refreshToken
        );
    }

    public AuthResponse authenticateWithGoogle(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            user = GoogleUserAdapter.adaptGoogleUser(oAuth2User)
                    .role(RoleType.RESTAURATEUR)
                    .status(AccountStatus.ACTIVE)
                    .provider("google")
                    .build();
            user = userRepository.save(user);
            final User savedUser = user;
            authEventListeners.forEach(l -> l.onUserRegistered(savedUser));
        }

        if (user.getOauthId() == null) {
            throw new IllegalArgumentException("use_email_password");
        }

        if (user.getStatus() == AccountStatus.SUSPENDED) {
            throw new SecurityException("Account is suspended.");
        }

        String token = jwtService.generateToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        boolean hasRestaurant = restaurantRepository.existsByOwner(user);

        return new AuthResponse(
                "Google login successful",
                user.getEmail(),
                user.getFullname(),
                user.getRole(),
                user.getStatus(),
                user.getProvider(),
                hasRestaurant,
                token,
                refreshToken
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
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        boolean hasRestaurant = restaurantRepository.existsByOwner(user);

        return new AuthResponse(
                "Google account linked successfully",
                user.getEmail(),
                user.getFullname(),
                user.getRole(),
                user.getStatus(),
                user.getProvider(),
                hasRestaurant,
                token,
                refreshToken
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
                null, null, null, null, null, false, null, null
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
                null,
                null
        );
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token is required");
        }

        String cleanToken = refreshToken.startsWith("Bearer ") ? refreshToken.substring(7) : refreshToken;
        if (tokenBlacklistService.isTokenBlacklisted(cleanToken)) {
            throw new IllegalArgumentException("Refresh token is invalid");
        }

        String email = jwtService.extractEmail(cleanToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getStatus() == AccountStatus.SUSPENDED) {
            throw new SecurityException("Account is suspended.");
        }

        if (!jwtService.isTokenValid(cleanToken, user.getEmail())) {
            throw new IllegalArgumentException("Refresh token is invalid");
        }

        String newAccessToken = jwtService.generateToken(user.getEmail());
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());
        tokenBlacklistService.blacklistToken(cleanToken);
        boolean hasRestaurant = restaurantRepository.existsByOwner(user);

        return new AuthResponse(
                "Token refreshed",
                user.getEmail(),
                user.getFullname(),
                user.getRole(),
                user.getStatus(),
                user.getProvider(),
                hasRestaurant,
                newAccessToken,
                newRefreshToken
        );
    }

    public AuthResponse authenticateWithGoogleToken(String idToken) {
        if (idToken == null || idToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idToken is required");
        }

        Map response = new RestTemplate().getForObject(
                "https://oauth2.googleapis.com/tokeninfo?id_token={token}",
                Map.class,
                idToken
        );

        String email = response != null ? (String) response.get("email") : null;
        String name = response != null ? (String) response.get("name") : null;
        String googleId = response != null ? (String) response.get("sub") : null;

        if (email == null || googleId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google token");
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            user = User.builder()
                    .email(email)
                    .fullname(name)
                    .oauthId(googleId)
                    .role(RoleType.RESTAURATEUR)
                    .status(AccountStatus.ACTIVE)
                    .provider("google")
                    .build();
            user = userRepository.save(user);
            final User savedUser = user;
            authEventListeners.forEach(l -> l.onUserRegistered(savedUser));
        } else if (user.getOauthId() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This email is already registered with email and password. Please sign in with your email and password instead.");
        }

        if (user.getStatus() == AccountStatus.SUSPENDED) {
            throw new SecurityException("Account is suspended.");
        }

        String token = jwtService.generateToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        boolean hasRestaurant = restaurantRepository.existsByOwner(user);

        return new AuthResponse(
                "Google login successful",
                user.getEmail(),
                user.getFullname(),
                user.getRole(),
                user.getStatus(),
                user.getProvider(),
                hasRestaurant,
                token,
                refreshToken
        );
    }
}
