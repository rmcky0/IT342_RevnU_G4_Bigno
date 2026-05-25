package com.revnu.backend.features.staff;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.staff.controller.SalaryController;
import com.revnu.backend.features.staff.controller.StaffController;
import com.revnu.backend.features.staff.dto.SalaryRequest;
import com.revnu.backend.features.staff.dto.SalaryResponse;
import com.revnu.backend.features.staff.dto.StaffProfileResponse;
import com.revnu.backend.features.staff.dto.StaffRequest;
import com.revnu.backend.features.staff.model.SalaryStatus;
import com.revnu.backend.features.staff.service.StaffService;
import com.revnu.backend.shared.config.OAuth2FailureHandler;
import com.revnu.backend.shared.config.OAuth2SuccessHandler;
import com.revnu.backend.shared.config.SecurityConfig;
import com.revnu.backend.shared.security.JwtAuthenticationFilter;
import com.revnu.backend.shared.security.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(controllers = {StaffController.class, SalaryController.class}, excludeAutoConfiguration = OAuth2ClientWebSecurityAutoConfiguration.class)
@TestPropertySource(properties = "app.frontend-url=http://localhost:3000")
@Import(SecurityConfig.class)
@DisplayName("StaffController Integration Tests")
class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private StaffService staffService;
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

    private StaffProfileResponse mockStaffResponse() {
        return new StaffProfileResponse(UUID.randomUUID(), "John Doe", "Waiter",
                new BigDecimal("500.00"), Collections.emptyList());
    }

    private SalaryResponse mockSalaryResponse() {
        return new SalaryResponse(UUID.randomUUID(), UUID.randomUUID(), "John Doe",
                new BigDecimal("500.00"), LocalDate.now(), SalaryStatus.OPEN, LocalDateTime.now());
    }

    // ── POST /revnu/staff — add staff member ──────────────────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("POST /staff - returns 200 when RESTAURATEUR adds a staff member")
    void addStaff_asRestaurateur_returns200() throws Exception {
        StaffRequest req = new StaffRequest("Jane Smith", "Cashier", new BigDecimal("450.00"));
        when(staffService.addStaff(anyString(), any())).thenReturn(mockStaffResponse());

        mockMvc.perform(post("/revnu/staff")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullname").value("John Doe"));
    }

    // ── Business Rule 1.2: ADMIN cannot manage staff ──────────────────────────────
    @Test
    @WithMockUser(username = "admin@revnu.com", roles = "ADMIN")
    @DisplayName("Rule 1.2 - POST /staff - returns 403 when ADMIN tries to add staff")
    void addStaff_asAdmin_returns403() throws Exception {
        StaffRequest req = new StaffRequest("Jane Smith", "Cashier", new BigDecimal("450.00"));

        mockMvc.perform(post("/revnu/staff")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    // ── GET /revnu/staff — list staff members ─────────────────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("GET /staff - returns 200 with list of active staff")
    void getStaff_asRestaurateur_returns200() throws Exception {
        when(staffService.getStaff(anyString())).thenReturn(List.of(mockStaffResponse()));

        mockMvc.perform(get("/revnu/staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ── DELETE /revnu/staff/{id} — delete staff ───────────────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("DELETE /staff/{id} - returns 200 on successful delete")
    void deleteStaff_asRestaurateur_returns200() throws Exception {
        UUID staffId = UUID.randomUUID();
        doNothing().when(staffService).deleteStaff(anyString(), any());

        mockMvc.perform(delete("/revnu/staff/" + staffId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("Staff member deleted successfully"));
    }

    // ── Business Rule 12: Salary rate must be positive ───────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("Rule 12 - POST /staff - returns 400 when salary rate is negative")
    void addStaff_negativeSalaryRate_returns400() throws Exception {
        StaffRequest req = new StaffRequest("Jane", "Cashier", new BigDecimal("-100.00"));

        mockMvc.perform(post("/revnu/staff")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ── POST /revnu/salaries — record payout ─────────────────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("POST /salaries - returns 200 when RESTAURATEUR records a salary payout")
    void recordSalaryPayout_asRestaurateur_returns200() throws Exception {
        SalaryRequest req = new SalaryRequest(UUID.randomUUID(), new BigDecimal("500.00"), LocalDate.now());
        when(staffService.recordSalaryPayout(anyString(), any())).thenReturn(mockSalaryResponse());

        mockMvc.perform(post("/revnu/salaries")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("OPEN"));
    }

    // ── Business Rule 1.2: ADMIN cannot record salary payouts ─────────────────────
    @Test
    @WithMockUser(username = "admin@revnu.com", roles = "ADMIN")
    @DisplayName("Rule 1.2 - POST /salaries - returns 403 when ADMIN tries to record salary payout")
    void recordSalaryPayout_asAdmin_returns403() throws Exception {
        SalaryRequest req = new SalaryRequest(UUID.randomUUID(), new BigDecimal("500.00"), LocalDate.now());

        mockMvc.perform(post("/revnu/salaries")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @BeforeEach
    void setUpFilter() throws Exception {
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);

            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }
}
