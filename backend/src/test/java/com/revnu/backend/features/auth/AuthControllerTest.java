package com.revnu.backend.features.auth;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.revnu.backend.features.auth.controller.AuthController;
import com.revnu.backend.features.auth.dto.AuthResponse;
import com.revnu.backend.features.auth.dto.LoginRequest;
import com.revnu.backend.features.auth.dto.RegisterRequest;
import com.revnu.backend.features.auth.model.AccountStatus;
import com.revnu.backend.features.auth.model.RoleType;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.auth.service.AuthService;
import com.revnu.backend.features.auth.service.PasswordResetService;
import com.revnu.backend.shared.config.OAuth2FailureHandler;
import com.revnu.backend.shared.config.OAuth2SuccessHandler;
import com.revnu.backend.shared.config.SecurityConfig;
import com.revnu.backend.shared.security.JwtAuthenticationFilter;
import com.revnu.backend.shared.security.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = OAuth2ClientWebSecurityAutoConfiguration.class)
@TestPropertySource(properties = "app.frontend-url=http://localhost:3000")
@Import(SecurityConfig.class)
@DisplayName("AuthController Integration Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private PasswordResetService passwordResetService;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;
    @MockitoBean
    private OAuth2SuccessHandler oAuth2SuccessHandler;
    @MockitoBean
    private OAuth2FailureHandler oAuth2FailureHandler;
    @MockitoBean
    private CacheManager cacheManager;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private AuthResponse mockAuthResponse(String message) {
        return new AuthResponse(message, "owner@test.com", "Test Owner",
                RoleType.RESTAURATEUR, AccountStatus.ACTIVE, "local",
                true, "access-token", "refresh-token");
    }

    // ── POST /revnu/auth/register — public endpoint ───────────────────────────────
    @Test
    @DisplayName("POST /register - returns 200 with AuthResponse on success")
    void register_validRequest_returns200() throws Exception {
        RegisterRequest req = new RegisterRequest("new@test.com", "password123", "Test User");
        when(authService.register(any())).thenReturn(mockAuthResponse("Registration successful"));

        mockMvc.perform(post("/revnu/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.message").value("Registration successful"))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"));
    }

    // ── POST /revnu/auth/login — public endpoint ──────────────────────────────────
    @Test
    @DisplayName("POST /login - returns 200 with JWT tokens on success")
    void login_validCredentials_returns200WithTokens() throws Exception {
        LoginRequest req = new LoginRequest("owner@test.com", "password123");
        when(authService.login(any())).thenReturn(mockAuthResponse("Login successful"));

        mockMvc.perform(post("/revnu/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));
    }

    // ── POST /revnu/auth/logout — public endpoint ─────────────────────────────────
    @Test
    @DisplayName("POST /logout - returns 200 on success")
    void logout_withToken_returns200() throws Exception {
        AuthResponse logoutResponse = new AuthResponse("Logout successful",
                null, null, null, null, null, false, null, null);
        when(authService.logout(anyString())).thenReturn(logoutResponse);

        mockMvc.perform(post("/revnu/auth/logout")
                .header("Authorization", "Bearer some-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("Logout successful"));
    }

    // ── GET /revnu/auth/me — requires authentication ──────────────────────────────
    @Test
    @DisplayName("GET /me - returns 200 with user details when authenticated")
    void getCurrentUser_authenticated_returns200() throws Exception {
        when(authService.getCurrentUser("owner@test.com")).thenReturn(mockAuthResponse("Current user"));

        mockMvc.perform(get("/revnu/auth/me")
                .with(user("owner@test.com").roles("RESTAURATEUR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("owner@test.com"))
                .andExpect(jsonPath("$.data.role").value("RESTAURATEUR"));
    }

    // ── GET /revnu/auth/me — unauthenticated returns 401/403 ─────────────────────
    @Test
    @DisplayName("GET /me - returns 401 when not authenticated")
    void getCurrentUser_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/revnu/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /revnu/auth/forgot-password — public endpoint ───────────────────────
    @Test
    @DisplayName("POST /forgot-password - returns 200 when email is valid")
    void forgotPassword_validEmail_returns200() throws Exception {
        Map<String, String> req = Map.of("email", "owner@test.com");

        mockMvc.perform(post("/revnu/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @BeforeEach
    void setUpFilter() throws Exception {
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);

            // Pass the request to the next filter in the chain
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }
}
