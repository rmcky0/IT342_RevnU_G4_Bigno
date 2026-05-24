package com.revnu.backend.features.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.revnu.backend.features.admin.controller.AdminController;
import com.revnu.backend.features.admin.dto.AdminUserResponse;
import com.revnu.backend.features.admin.dto.UpdateUserStatusRequest;
import com.revnu.backend.features.admin.service.AdminService;
import com.revnu.backend.features.auth.model.AccountStatus;
import com.revnu.backend.features.auth.model.RoleType;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.shared.config.OAuth2FailureHandler;
import com.revnu.backend.shared.config.OAuth2SuccessHandler;
import com.revnu.backend.shared.config.SecurityConfig;
import com.revnu.backend.shared.security.JwtAuthenticationFilter;
import com.revnu.backend.shared.security.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(controllers = AdminController.class, excludeAutoConfiguration = OAuth2ClientWebSecurityAutoConfiguration.class)
@TestPropertySource(properties = "app.frontend-url=http://localhost:3000")
@Import(SecurityConfig.class)
@DisplayName("AdminController Integration Tests")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private AdminService adminService;
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

    private AdminUserResponse mockUserResponse() {
        return new AdminUserResponse(UUID.randomUUID(), "owner@test.com", "Test Owner",
                RoleType.RESTAURATEUR, AccountStatus.ACTIVE, LocalDateTime.now(), null, null);
    }

    // ── Business Rule 1.3: RESTAURATEUR cannot access admin endpoints ─────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("Rule 1.3 - GET /admin/users - returns 403 when RESTAURATEUR accesses admin endpoint")
    void getAllUsers_asRestaurateur_returns403() throws Exception {
        mockMvc.perform(get("/revnu/admin/users"))
                .andExpect(status().isForbidden());
    }

    // ── GET /revnu/admin/users — ADMIN can list users ─────────────────────────────
    @Test
    @WithMockUser(username = "admin@revnu.com", roles = "ADMIN")
    @DisplayName("GET /admin/users - returns 200 with paginated user list when ADMIN")
    void getAllUsers_asAdmin_returns200() throws Exception {
        when(adminService.getAllUsers(anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(mockUserResponse()), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/revnu/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ── PUT /revnu/admin/users/{id}/status — update user status ───────────────────
    @Test
    @WithMockUser(username = "admin@revnu.com", roles = "ADMIN")
    @DisplayName("PUT /admin/users/{id}/status - returns 200 when ADMIN suspends a user")
    void updateUserStatus_asAdmin_returns200() throws Exception {
        UUID userId = UUID.randomUUID();
        UpdateUserStatusRequest req = new UpdateUserStatusRequest(AccountStatus.SUSPENDED);
        AdminUserResponse suspended = new AdminUserResponse(userId, "owner@test.com", "Test Owner",
                RoleType.RESTAURATEUR, AccountStatus.SUSPENDED, LocalDateTime.now(), null, null);
        when(adminService.updateUserStatus(eq(userId), any())).thenReturn(suspended);

        mockMvc.perform(put("/revnu/admin/users/" + userId + "/status")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SUSPENDED"));
    }

    // ── Business Rule 1.3: RESTAURATEUR cannot suspend users ──────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("Rule 1.3 - PUT /admin/users/{id}/status - returns 403 for RESTAURATEUR")
    void updateUserStatus_asRestaurateur_returns403() throws Exception {
        UUID userId = UUID.randomUUID();
        UpdateUserStatusRequest req = new UpdateUserStatusRequest(AccountStatus.SUSPENDED);

        mockMvc.perform(put("/revnu/admin/users/" + userId + "/status")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    // ── DELETE /revnu/admin/users/{id} — delete a user ────────────────────────────
    @Test
    @WithMockUser(username = "admin@revnu.com", roles = "ADMIN")
    @DisplayName("DELETE /admin/users/{id} - returns 200 when ADMIN deletes a user")
    void deleteUser_asAdmin_returns200() throws Exception {
        UUID userId = UUID.randomUUID();
        doNothing().when(adminService).deleteUser(userId);

        mockMvc.perform(delete("/revnu/admin/users/" + userId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("User deleted successfully."));
    }

    // ── GET /revnu/admin/stats — platform statistics ───────────────────────────────
    @Test
    @WithMockUser(username = "admin@revnu.com", roles = "ADMIN")
    @DisplayName("GET /admin/stats - returns 200 with platform stats when ADMIN")
    void getStats_asAdmin_returns200() throws Exception {
        mockMvc.perform(get("/revnu/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ── GET /revnu/admin/restaurants — list all restaurants ───────────────────────
    @Test
    @WithMockUser(username = "admin@revnu.com", roles = "ADMIN")
    @DisplayName("GET /admin/restaurants - returns 200 with paginated restaurant list")
    void getAllRestaurants_asAdmin_returns200() throws Exception {
        when(adminService.getAllRestaurants(anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

        mockMvc.perform(get("/revnu/admin/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ── Unauthenticated requests are rejected ─────────────────────────────────────
    @Test
    @DisplayName("GET /admin/users - returns 401 when not authenticated")
    void getAllUsers_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/revnu/admin/users"))
                .andExpect(status().isUnauthorized());
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
