package com.revnu.backend.features.categories;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;

import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.categories.controller.CategoryController;
import com.revnu.backend.features.categories.dto.CategoryRequest;
import com.revnu.backend.features.categories.dto.CategoryResponse;
import com.revnu.backend.features.categories.service.CategoryService;
import com.revnu.backend.shared.config.OAuth2FailureHandler;
import com.revnu.backend.shared.config.OAuth2SuccessHandler;
import com.revnu.backend.shared.config.SecurityConfig;
import com.revnu.backend.shared.security.JwtAuthenticationFilter;
import com.revnu.backend.shared.security.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(controllers = CategoryController.class, excludeAutoConfiguration = OAuth2ClientWebSecurityAutoConfiguration.class)
@TestPropertySource(properties = "app.frontend-url=http://localhost:3000")
@Import(SecurityConfig.class)
@DisplayName("CategoryController Integration Tests")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;
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

    private CategoryResponse mockCategoryResponse(boolean isDefault) {
        return new CategoryResponse(UUID.randomUUID(), "Meals", "SALE", isDefault,
                isDefault ? null : UUID.randomUUID());
    }

    // ── GET /revnu/categories — list visible categories ────────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("GET /categories - returns 200 with list of categories")
    void getCategories_asRestaurateur_returns200() throws Exception {
        when(categoryService.getCategories(anyString(), isNull()))
                .thenReturn(List.of(mockCategoryResponse(true), mockCategoryResponse(false)));

        mockMvc.perform(get("/revnu/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ── POST /revnu/categories — create custom category ───────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("POST /categories - returns 201 on successful category creation")
    void createCategory_asRestaurateur_returns201() throws Exception {
        CategoryRequest req = new CategoryRequest("Custom Sales", "SALE");
        when(categoryService.createCategory(anyString(), any())).thenReturn(mockCategoryResponse(false));

        mockMvc.perform(post("/revnu/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isDefault").value(false));
    }

    // ── Business Rule 3: Predefined category update returns 403 ──────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("Rule 3 - PUT /categories/{id} - returns 403 when editing predefined category")
    void updateCategory_predefinedCategory_returns403() throws Exception {
        UUID catId = UUID.randomUUID();
        CategoryRequest req = new CategoryRequest("Renamed", "SALE");
        when(categoryService.updateCategory(anyString(), eq(catId), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Predefined categories cannot be renamed."));

        mockMvc.perform(put("/revnu/categories/" + catId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    // ── Business Rule 3: In-use category deletion returns 409 Conflict ────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("Rule 3 - DELETE /categories/{id} - returns 409 when category is in use")
    void deleteCategory_inUse_returns409() throws Exception {
        UUID catId = UUID.randomUUID();
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Category is in use by existing records and cannot be deleted."))
                .when(categoryService).deleteCategory(anyString(), eq(catId));

        mockMvc.perform(delete("/revnu/categories/" + catId)
                .with(csrf()))
                .andExpect(status().isConflict());
    }

    // ── DELETE /revnu/categories/{id} — not in use returns 200 ───────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("DELETE /categories/{id} - returns 200 when category is unused")
    void deleteCategory_notInUse_returns200() throws Exception {
        UUID catId = UUID.randomUUID();
        doNothing().when(categoryService).deleteCategory(anyString(), eq(catId));

        mockMvc.perform(delete("/revnu/categories/" + catId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleted").value(true));
    }

    // ── Business Rule 1.2: ADMIN cannot access category endpoints ─────────────────
    @Test
    @WithMockUser(username = "admin@revnu.com", roles = "ADMIN")
    @DisplayName("Rule 1.2 - POST /categories - returns 403 when ADMIN tries to create category")
    void createCategory_asAdmin_returns403() throws Exception {
        CategoryRequest req = new CategoryRequest("Admin Cat", "SALE");

        mockMvc.perform(post("/revnu/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    // ── Request validation: blank name is rejected ────────────────────────────────
    @Test
    @WithMockUser(username = "owner@test.com", roles = "RESTAURATEUR")
    @DisplayName("POST /categories - returns 400 when category name is blank")
    void createCategory_blankName_returns400() throws Exception {
        CategoryRequest req = new CategoryRequest("", "SALE");

        mockMvc.perform(post("/revnu/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
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
