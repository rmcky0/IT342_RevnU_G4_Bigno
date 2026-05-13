package com.revnu.backend.features.auth;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;
import com.revnu.backend.shared.security.JwtService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
        @Mock
        private TokenBlacklistService tokenBlacklistService;
    @Mock
    private RegistrationValidationPipeline validationPipeline;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AuthService authFacade;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setEmail("tenant@revnu.com");
        mockUser.setFullname("Test Tenant");
        mockUser.setPasswordHash("$2a$10$hashed_password_here");
        mockUser.setRole(RoleType.TENANT);
    }

    @Test
    @DisplayName("Login with valid credentials returns token")
    void login_validCredentials_returnsAuthResponse() {
        when(userRepository.findByEmail("tenant@revnu.com"))
                .thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password123", mockUser.getPasswordHash()))
                .thenReturn(true);
        when(jwtService.generateToken(any()))
                .thenReturn("mock.jwt.token");

        AuthResponse result = authFacade.login(
                new LoginRequest("tenant@revnu.com", "password123"));

        assertNotNull(result, "Response should not be null");
        assertEquals("mock.jwt.token", result.accessToken(), "Token should match");
        assertEquals("tenant@revnu.com", result.email(), "Email should match");

        verify(userRepository, times(1)).findByEmail("tenant@revnu.com");
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(jwtService, times(1)).generateToken(any());
    }

    @Test
    @DisplayName("Login with wrong password throws exception")
    void login_wrongPassword_throwsRuntimeException() {
        when(userRepository.findByEmail("tenant@revnu.com"))
                .thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrongpassword", mockUser.getPasswordHash()))
                .thenReturn(false);

        assertThrows(RuntimeException.class, ()
                -> authFacade.login(new LoginRequest("tenant@revnu.com", "wrongpassword")),
                "Should throw RuntimeException for wrong password"
        );

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Login with unknown email throws exception")
    void login_unknownEmail_throwsRuntimeException() {
        when(userRepository.findByEmail("unknown@revnu.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, ()
                -> authFacade.login(new LoginRequest("unknown@revnu.com", "anypassword")),
                "Should throw RuntimeException for unknown email"
        );

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Register new user saves and returns token")
    void register_newUser_savesUserAndReturnsToken() {
        RegisterRequest request = new RegisterRequest(
                "newuser@revnu.com", "NewUser", "securepassword");

        doNothing().when(validationPipeline).validateRegistration(any());
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtService.generateToken(any())).thenReturn("new.jwt.token");

        AuthResponse result = authFacade.register(request);

        assertNotNull(result, "Response should not be null");
        assertNotNull(result.accessToken(), "Token should not be null");

        verify(validationPipeline, times(1)).validateRegistration(any());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    @DisplayName("Register with duplicate email throws exception")
    void register_duplicateEmail_throwsRuntimeException() {
        RegisterRequest request = new RegisterRequest(
                "tenant@revnu.com", "Duplicate", "password");

        doThrow(new RuntimeException("Email already exists"))
                .when(validationPipeline).validateRegistration(any());

        assertThrows(RuntimeException.class, ()
                -> authFacade.register(request),
                "Should throw RuntimeException for duplicate email"
        );

        verify(validationPipeline, times(1)).validateRegistration(any());
        verify(userRepository, never()).save(any(User.class));
    }

        @Test
        @DisplayName("Logout blacklists bearer token and returns success response")
        void logout_blacklistsBearerTokenAndReturnsSuccess() {
                AuthResponse result = authFacade.logout("Bearer token-abc");

                assertEquals("Logout successful", result.message());
                verify(tokenBlacklistService).blacklistToken("token-abc");
                verify(jwtService, never()).generateToken(anyString());
                verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Current user returns profile data and restaurant flag")
        void getCurrentUser_returnsProfileData() {
                mockUser.setStatus(AccountStatus.ACTIVE);
                mockUser.setProvider("local");
                when(userRepository.findByEmail("tenant@revnu.com")).thenReturn(Optional.of(mockUser));
                when(restaurantRepository.existsByOwner(mockUser)).thenReturn(true);

                AuthResponse result = authFacade.getCurrentUser("tenant@revnu.com");

                assertEquals("Current user", result.message());
                assertEquals("tenant@revnu.com", result.email());
                assertEquals("Test Tenant", result.fullname());
                assertTrue(result.hasRestaurant());
                verify(restaurantRepository).existsByOwner(mockUser);
        }
}
