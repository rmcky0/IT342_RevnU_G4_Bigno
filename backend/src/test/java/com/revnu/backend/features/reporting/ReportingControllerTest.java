package com.revnu.backend.features.reporting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.reporting.controller.ReportingController;
import com.revnu.backend.features.reporting.dto.CloseDayRequest;
import com.revnu.backend.features.reporting.dto.CloseDayResponse;
import com.revnu.backend.features.reporting.dto.DailySummaryDto;
import com.revnu.backend.features.reporting.service.ReportingService;
import com.revnu.backend.shared.config.OAuth2FailureHandler;
import com.revnu.backend.shared.config.OAuth2SuccessHandler;
import com.revnu.backend.shared.config.SecurityConfig;
import com.revnu.backend.shared.security.JwtAuthenticationFilter;
import com.revnu.backend.shared.security.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(controllers = ReportingController.class, excludeAutoConfiguration = OAuth2ClientWebSecurityAutoConfiguration.class)
@TestPropertySource(properties = "app.frontend-url=http://localhost:3000")
@Import(SecurityConfig.class)
@DisplayName("ReportingController Integration Tests")
class ReportingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private ReportingService reportingService;

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

    private DailySummaryDto mockSummaryDto() {
        return new DailySummaryDto(LocalDate.now(),
                new BigDecimal("1000.00"), new BigDecimal("300.00"),
                new BigDecimal("200.00"), new BigDecimal("500.00"), true);
    }

    // ── POST /revnu/day/close — EOD trigger ────────────────────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("POST /day/close - returns 200 when RESTAURATEUR initiates EOD close")
    void closeDay_asRestaurateur_returns200() throws Exception {
        CloseDayRequest req = new CloseDayRequest(LocalDate.now());
        CloseDayResponse response = new CloseDayResponse(mockSummaryDto(), true);
        when(reportingService.closeDay(anyString(), any())).thenReturn(response);

        mockMvc.perform(post("/revnu/day/close")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reportSent").value(true));
    }

    // ── Business Rule 1.2: ADMIN cannot initiate EOD ──────────────────────────────
    @Test
    @WithMockUser(username = "admin@revnu.com", roles = "ADMIN")
    @DisplayName("Rule 1.2 - POST /day/close - returns 403 when ADMIN tries to close the day")
    void closeDay_asAdmin_returns403() throws Exception {
        CloseDayRequest req = new CloseDayRequest(LocalDate.now());

        mockMvc.perform(post("/revnu/day/close")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    // ── POST /revnu/day/close — unauthenticated returns 401 ───────────────────────
    @Test
    @DisplayName("POST /day/close - returns 401 when not authenticated")
    void closeDay_unauthenticated_returns401() throws Exception {
        CloseDayRequest req = new CloseDayRequest(LocalDate.now());

        mockMvc.perform(post("/revnu/day/close")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    // ── GET /revnu/day/summary/{date} — retrieve EOD summary ──────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("GET /day/summary/{date} - returns 200 with summary data")
    void getSummary_asRestaurateur_returns200() throws Exception {
        LocalDate date = LocalDate.of(2024, 5, 15);
        when(reportingService.getSummary(anyString(), eq(date))).thenReturn(mockSummaryDto());

        mockMvc.perform(get("/revnu/day/summary/" + date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalSales").value(1000.00));
    }

    // ── GET /revnu/day/summaries — all EOD summaries ──────────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("GET /day/summaries - returns 200 with all historical summaries")
    void getAllSummaries_asRestaurateur_returns200() throws Exception {
        when(reportingService.getAllSummaries(anyString())).thenReturn(List.of(mockSummaryDto()));

        mockMvc.perform(get("/revnu/day/summaries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ── Business Rule 10: Double-close returns 409 (service throws IllegalState) ──
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("Rule 10 - POST /day/close - returns 409 Conflict when day is already closed")
    void closeDay_alreadyClosed_serviceThrowsIllegalState() throws Exception {
        CloseDayRequest req = new CloseDayRequest(LocalDate.now());
        when(reportingService.closeDay(anyString(), any()))
                .thenThrow(new IllegalStateException("Records for 2024-05-15 are already closed."));

        mockMvc.perform(post("/revnu/day/close")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
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
