package com.revnu.backend.features.expenses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.expenses.controller.ExpenseController;
import com.revnu.backend.features.expenses.dto.ExpenseRequest;
import com.revnu.backend.features.expenses.dto.ExpenseResponse;
import com.revnu.backend.features.expenses.model.ExpenseStatus;
import com.revnu.backend.features.expenses.service.ExpenseService;
import com.revnu.backend.shared.config.OAuth2FailureHandler;
import com.revnu.backend.shared.config.OAuth2SuccessHandler;
import com.revnu.backend.shared.config.SecurityConfig;
import com.revnu.backend.shared.security.JwtAuthenticationFilter;
import com.revnu.backend.shared.security.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(controllers = ExpenseController.class, excludeAutoConfiguration = OAuth2ClientWebSecurityAutoConfiguration.class)
@TestPropertySource(properties = "app.frontend-url=http://localhost:3000")
@Import(SecurityConfig.class)
@DisplayName("ExpenseController Integration Tests")
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private ExpenseService expenseService;

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

    private ExpenseResponse mockExpenseResponse() {
        return new ExpenseResponse(UUID.randomUUID(), new BigDecimal("150.00"),
                UUID.randomUUID(), "Utilities", "Electric bill", null,
                ExpenseStatus.OPEN, LocalDateTime.now());
    }

    // ── POST /revnu/expenses — RESTAURATEUR required ──────────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("POST /expenses - returns 200 when RESTAURATEUR records an expense")
    void recordExpense_asRestaurateur_returns200() throws Exception {
        ExpenseRequest req = new ExpenseRequest(new BigDecimal("150.00"), UUID.randomUUID(), "Electric bill");
        when(expenseService.recordExpense(anyString(), any())).thenReturn(mockExpenseResponse());

        mockMvc.perform(post("/revnu/expenses")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("OPEN"));
    }

    // ── Business Rule 1.2: ADMIN cannot access expense endpoints ─────────────────
    @Test
    @WithMockUser(username = "admin@revnu.com", roles = "ADMIN")
    @DisplayName("Rule 1.2 - POST /expenses - returns 403 when ADMIN tries to record expense")
    void recordExpense_asAdmin_returns403() throws Exception {
        ExpenseRequest req = new ExpenseRequest(new BigDecimal("150.00"), UUID.randomUUID(), null);

        mockMvc.perform(post("/revnu/expenses")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    // ── Business Rule 12: Positive amount validation ──────────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("Rule 12 - POST /expenses - returns 400 when amount is zero or negative")
    void recordExpense_zeroAmount_returns400() throws Exception {
        ExpenseRequest req = new ExpenseRequest(BigDecimal.ZERO, UUID.randomUUID(), null);

        mockMvc.perform(post("/revnu/expenses")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ── GET /revnu/expenses — returns paginated list ──────────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("GET /expenses - returns 200 with paginated results")
    void getExpenses_asRestaurateur_returns200() throws Exception {
        when(expenseService.getAllExpenses(anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(mockExpenseResponse()), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/revnu/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ── PUT /revnu/expenses/{id} — update ────────────────────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("PUT /expenses/{id} - returns 200 on successful update")
    void updateExpense_asRestaurateur_returns200() throws Exception {
        UUID expenseId = UUID.randomUUID();
        ExpenseRequest req = new ExpenseRequest(new BigDecimal("200.00"), UUID.randomUUID(), "Updated");
        when(expenseService.updateExpense(anyString(), any(), any())).thenReturn(mockExpenseResponse());

        mockMvc.perform(put("/revnu/expenses/" + expenseId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ── DELETE /revnu/expenses/{id} — delete ─────────────────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("DELETE /expenses/{id} - returns 200 on successful delete")
    void deleteExpense_asRestaurateur_returns200() throws Exception {
        UUID expenseId = UUID.randomUUID();
        doNothing().when(expenseService).deleteExpense(anyString(), any());

        mockMvc.perform(delete("/revnu/expenses/" + expenseId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("Expense deleted successfully"));
    }

    // ── POST /revnu/expenses — unauthenticated returns 401 ───────────────────────
    @Test
    @DisplayName("POST /expenses - returns 401 when not authenticated")
    void recordExpense_unauthenticated_returns401() throws Exception {
        ExpenseRequest req = new ExpenseRequest(new BigDecimal("150.00"), UUID.randomUUID(), null);

        mockMvc.perform(post("/revnu/expenses")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
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
