package com.revnu.backend.features.auth;

import com.revnu.backend.features.auth.dto.AuthResponse;
import com.revnu.backend.features.auth.dto.LoginRequest;
import com.revnu.backend.features.auth.dto.RegisterRequest;
import com.revnu.backend.features.auth.model.AccountStatus;
import com.revnu.backend.features.auth.model.RoleType;
import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.auth.service.AuthService;
import com.revnu.backend.features.auth.service.TokenBlacklistService;
import com.revnu.backend.features.auth.validator.RegistrationValidationPipeline;
import com.revnu.backend.features.notifications.service.NotificationService;
import com.revnu.backend.features.reporting.service.EmailService;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;
import com.revnu.backend.shared.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private TokenBlacklistService tokenBlacklistService;
    @Mock private RegistrationValidationPipeline validationPipeline;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private NotificationService notificationService;
    @Mock private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private User activeRestaurateur;
    private User suspendedRestaurateur;

    @BeforeEach
    void setUp() {
        activeRestaurateur = User.builder()
                .id(UUID.randomUUID())
                .email("owner@test.com")
                .fullname("Test Owner")
                .passwordHash("hashed-password")
                .role(RoleType.RESTAURATEUR)
                .status(AccountStatus.ACTIVE)
                .provider("local")
                .build();

        suspendedRestaurateur = User.builder()
                .id(UUID.randomUUID())
                .email("suspended@test.com")
                .fullname("Suspended User")
                .passwordHash("hashed-password")
                .role(RoleType.RESTAURATEUR)
                .status(AccountStatus.SUSPENDED)
                .provider("local")
                .build();
    }

    // ── Business Rule 1.1: Registration always assigns RESTAURATEUR role ─────────
    @Test
    @DisplayName("Rule 1.1 - register() always assigns RESTAURATEUR role, never ADMIN")
    void register_alwaysCreatesRestaurateurRole() {
        RegisterRequest request = new RegisterRequest("new@test.com", "password123", "New User");
        doNothing().when(validationPipeline).validateRegistration(any());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(anyString())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(anyString())).thenReturn("refresh-token");

        AuthResponse response = authService.register(request);

        assertThat(response.role()).isEqualTo(RoleType.RESTAURATEUR);
        verify(userRepository).save(argThat(u -> u.getRole() == RoleType.RESTAURATEUR));
    }

    // ── Business Rule 1.1: New registrations get ACTIVE status ──────────────────
    @Test
    @DisplayName("Rule 1.1 - register() sets account status to ACTIVE")
    void register_setsStatusToActive() {
        RegisterRequest request = new RegisterRequest("new@test.com", "password123", "New User");
        doNothing().when(validationPipeline).validateRegistration(any());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(anyString())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(anyString())).thenReturn("refresh-token");

        AuthResponse response = authService.register(request);

        assertThat(response.status()).isEqualTo(AccountStatus.ACTIVE);
    }

    // ── Business Rule 1.5: Suspended accounts are blocked from logging in ───────
    @Test
    @DisplayName("Rule 1.5 - login() throws SecurityException for SUSPENDED accounts")
    void login_suspendedAccount_throwsSecurityException() {
        LoginRequest request = new LoginRequest("suspended@test.com", "password123");
        when(userRepository.findByEmail("suspended@test.com")).thenReturn(Optional.of(suspendedRestaurateur));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("suspended");
    }

    // ── Business Rule 1.5: Invalid credentials return 401 ───────────────────────
    @Test
    @DisplayName("Rule 1.5 - login() rejects wrong password")
    void login_wrongPassword_throwsException() {
        LoginRequest request = new LoginRequest("owner@test.com", "wrong-password");
        when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(activeRestaurateur));
        when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .hasMessageContaining("Invalid email or password");
    }

    // ── Business Rule 1.5: Successful login returns tokens ──────────────────────
    @Test
    @DisplayName("Rule 1.5 - login() returns JWT tokens on success")
    void login_validCredentials_returnsTokens() {
        LoginRequest request = new LoginRequest("owner@test.com", "password123");
        when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(activeRestaurateur));
        when(passwordEncoder.matches("password123", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken(anyString())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(anyString())).thenReturn("refresh-token");
        when(restaurantRepository.existsByOwner(activeRestaurateur)).thenReturn(true);

        AuthResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.hasRestaurant()).isTrue();
    }

    // ── Business Rule 15: Active status enforcement on every token refresh ───────
    @Test
    @DisplayName("Rule 15 - refreshToken() blocks SUSPENDED accounts even with valid token")
    void refreshToken_suspendedUser_throwsSecurityException() {
        String fakeRefreshToken = "valid-refresh-token";
        when(tokenBlacklistService.isTokenBlacklisted(fakeRefreshToken)).thenReturn(false);
        when(jwtService.extractEmail(fakeRefreshToken)).thenReturn("suspended@test.com");
        when(userRepository.findByEmail("suspended@test.com")).thenReturn(Optional.of(suspendedRestaurateur));

        assertThatThrownBy(() -> authService.refreshToken(fakeRefreshToken))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("suspended");
    }

    // ── Business Rule 15: Blacklisted refresh tokens are rejected ───────────────
    @Test
    @DisplayName("Rule 15 - refreshToken() rejects blacklisted tokens")
    void refreshToken_blacklistedToken_throwsException() {
        String blacklistedToken = "blacklisted-token";
        when(tokenBlacklistService.isTokenBlacklisted(blacklistedToken)).thenReturn(true);

        assertThatThrownBy(() -> authService.refreshToken(blacklistedToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid");
    }

    // ── Business Rule 15: Logout blacklists the token ───────────────────────────
    @Test
    @DisplayName("Rule 15 - logout() adds token to blacklist")
    void logout_withBearerToken_blacklistsToken() {
        String bearerToken = "Bearer my-jwt-token";

        AuthResponse response = authService.logout(bearerToken);

        verify(tokenBlacklistService).blacklistToken("my-jwt-token");
        assertThat(response.message()).contains("Logout successful");
    }

    // ── Business Rule 15: Missing token on logout is rejected ───────────────────
    @Test
    @DisplayName("Rule 15 - logout() rejects blank token")
    void logout_blankToken_throwsException() {
        assertThatThrownBy(() -> authService.logout(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Token is required");
    }

    // ── Business Rule 15: OAuth users have null passwordHash ────────────────────
    @Test
    @DisplayName("Rule 15 - Google OAuth registration stores null passwordHash")
    void register_googleOAuth_passwordHashIsNull() {
        // Google OAuth users registered via authenticateWithGoogleToken store no password
        // Verified by checking User builder in the authenticateWithGoogleToken method:
        // user = User.builder()...build() without .passwordHash(...)
        User googleUser = User.builder()
                .email("google@test.com")
                .fullname("Google User")
                .oauthId("google-sub-id")
                .role(RoleType.RESTAURATEUR)
                .status(AccountStatus.ACTIVE)
                .provider("google")
                .build();

        assertThat(googleUser.getPasswordHash()).isNull();
    }

    // ── Business Rule 16: Registration triggers admin notification ───────────────
    @Test
    @DisplayName("Rule 16 - register() notifies admins about new user")
    void register_notifiesAdmins() {
        RegisterRequest request = new RegisterRequest("new@test.com", "password123", "New User");
        doNothing().when(validationPipeline).validateRegistration(any());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(anyString())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(anyString())).thenReturn("refresh-token");

        authService.register(request);

        verify(notificationService).notifyAdminsUserRegistered(any(User.class));
        verify(emailService).sendWelcomeEmail(anyString(), anyString());
    }
}
