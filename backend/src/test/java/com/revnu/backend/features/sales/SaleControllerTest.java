package com.revnu.backend.features.sales;

import tools.jackson.databind.json.JsonMapper;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.sales.controller.SaleController;
import com.revnu.backend.features.sales.dto.SaleRequest;
import com.revnu.backend.features.sales.dto.SaleResponse;
import com.revnu.backend.features.sales.model.SaleStatus;
import com.revnu.backend.features.sales.service.SaleService;
import com.revnu.backend.shared.config.OAuth2FailureHandler;
import com.revnu.backend.shared.config.OAuth2SuccessHandler;
import com.revnu.backend.shared.config.SecurityConfig;
import com.revnu.backend.shared.security.JwtAuthenticationFilter;
import com.revnu.backend.shared.security.JwtService;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.ArgumentMatchers.any;

import com.revnu.backend.shared.config.SecurityConfig;
import com.revnu.backend.shared.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Import;

@WebMvcTest(controllers = SaleController.class, excludeAutoConfiguration = OAuth2ClientWebSecurityAutoConfiguration.class)
@TestPropertySource(properties = "app.frontend-url=http://localhost:3000")
@Import(SecurityConfig.class)
@DisplayName("SaleController Integration Tests")
class SaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private SaleService saleService;

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

    private SaleResponse mockSaleResponse() {
        return new SaleResponse(UUID.randomUUID(), new BigDecimal("500.00"),
                UUID.randomUUID(), "Meals", "Test sale", SaleStatus.OPEN, LocalDateTime.now());
    }

    // ── POST /revnu/sales — RESTAURATEUR required ─────────────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("POST /sales - returns 200 when RESTAURATEUR records a sale")
    void recordSale_asRestaurateur_returns200() throws Exception {
        SaleRequest req = new SaleRequest(new BigDecimal("500.00"), UUID.randomUUID(), "Lunch");
        when(saleService.recordSale(anyString(), any())).thenReturn(mockSaleResponse());

        mockMvc.perform(post("/revnu/sales")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("OPEN"));
    }

    // ── Business Rule 1.3: ADMIN cannot access sale endpoints ────────────────────
    @Test
    @WithMockUser(username = "admin@revnu.com", roles = "ADMIN")
    @DisplayName("Rule 1.3 - POST /sales - returns 403 when ADMIN tries to record a sale")
    void recordSale_asAdmin_returns403() throws Exception {
        SaleRequest req = new SaleRequest(new BigDecimal("500.00"), UUID.randomUUID(), "Lunch");

        mockMvc.perform(post("/revnu/sales")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    // ── POST /revnu/sales — unauthenticated returns 401 ──────────────────────────
    @Test
    @DisplayName("POST /sales - returns 401 when not authenticated")
    void recordSale_unauthenticated_returns401() throws Exception {
        SaleRequest req = new SaleRequest(new BigDecimal("500.00"), UUID.randomUUID(), "Lunch");

        mockMvc.perform(post("/revnu/sales")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    // ── Business Rule 12: Positive amount validation ──────────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("Rule 12 - POST /sales - returns 400 when amount is zero or negative")
    void recordSale_negativeAmount_returns400() throws Exception {
        SaleRequest req = new SaleRequest(new BigDecimal("-10.00"), UUID.randomUUID(), null);

        mockMvc.perform(post("/revnu/sales")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ── GET /revnu/sales — paginated ──────────────────────────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("GET /sales - returns 200 with paginated results")
    void getAllSales_asRestaurateur_returns200() throws Exception {
        when(saleService.getAllSales(anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(mockSaleResponse()), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/revnu/sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ── PUT /revnu/sales/{id} — update ────────────────────────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("PUT /sales/{id} - returns 200 on successful update")
    void updateSale_asRestaurateur_returns200() throws Exception {
        UUID saleId = UUID.randomUUID();
        SaleRequest req = new SaleRequest(new BigDecimal("600.00"), UUID.randomUUID(), "Updated");
        when(saleService.updateSale(anyString(), any(), any())).thenReturn(mockSaleResponse());

        mockMvc.perform(put("/revnu/sales/" + saleId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ── DELETE /revnu/sales/{id} — delete ─────────────────────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("DELETE /sales/{id} - returns 200 on successful delete")
    void deleteSale_asRestaurateur_returns200() throws Exception {
        UUID saleId = UUID.randomUUID();
        doNothing().when(saleService).deleteSale(anyString(), any());

        mockMvc.perform(delete("/revnu/sales/" + saleId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("Sale deleted successfully"));
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
