package com.revnu.backend.features.sales;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.categories.model.Category;
import com.revnu.backend.features.categories.repository.CategoryRepository;
import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;
import com.revnu.backend.features.sales.dto.SaleRequest;
import com.revnu.backend.features.sales.dto.SaleResponse;
import com.revnu.backend.features.sales.model.Sale;
import com.revnu.backend.features.sales.model.SaleStatus;
import com.revnu.backend.features.sales.repository.SaleRepository;
import com.revnu.backend.features.sales.service.SaleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SaleService Unit Tests")
class SaleServiceTest {

    @Mock private SaleRepository saleRepository;
    @Mock private UserRepository userRepository;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private CategoryRepository categoryRepository;

    @InjectMocks
    private SaleService saleService;

    private User owner;
    private Restaurant restaurant;
    private Restaurant otherRestaurant;
    private Category saleCategory;
    private Category expenseCategory;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(UUID.randomUUID()).email("owner@test.com").build();
        restaurant = Restaurant.builder().id(UUID.randomUUID()).name("My Restaurant").owner(owner).build();
        otherRestaurant = Restaurant.builder().id(UUID.randomUUID()).name("Other Restaurant").build();

        saleCategory = Category.builder()
                .id(UUID.randomUUID()).name("Meals").type("SALE")
                .restaurant(null).isDefault(true).build();

        expenseCategory = Category.builder()
                .id(UUID.randomUUID()).name("Utilities").type("EXPENSE")
                .restaurant(null).isDefault(true).build();

        when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
        when(restaurantRepository.findByOwner(owner)).thenReturn(Optional.of(restaurant));
    }

    // ── Business Rule 4: Records linked to valid category are created with OPEN status ──
    @Test
    @DisplayName("Rule 4 - recordSale() creates a sale with OPEN status")
    void recordSale_success_statusIsOpen() {
        SaleRequest request = new SaleRequest(new BigDecimal("500.00"), saleCategory.getId(), "Lunch");
        when(categoryRepository.findById(saleCategory.getId())).thenReturn(Optional.of(saleCategory));
        Sale savedSale = Sale.builder()
                .id(UUID.randomUUID()).amount(request.amount())
                .category(saleCategory).restaurant(restaurant).status(SaleStatus.OPEN).build();
        when(saleRepository.save(any(Sale.class))).thenReturn(savedSale);

        SaleResponse response = saleService.recordSale("owner@test.com", request);

        assertThat(response.status()).isEqualTo(SaleStatus.OPEN);
    }

    // ── Business Rule 12: SALE category segregation — EXPENSE category rejected ─
    @Test
    @DisplayName("Rule 12 - recordSale() rejects EXPENSE category type")
    void recordSale_withExpenseCategory_throwsBadRequest() {
        SaleRequest request = new SaleRequest(new BigDecimal("200.00"), expenseCategory.getId(), null);
        when(categoryRepository.findById(expenseCategory.getId())).thenReturn(Optional.of(expenseCategory));

        assertThatThrownBy(() -> saleService.recordSale("owner@test.com", request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST));
    }

    // ── Business Rule 2: Tenant Isolation — category must belong to the restaurant ──
    @Test
    @DisplayName("Rule 2 - recordSale() rejects category belonging to another restaurant")
    void recordSale_categoryFromAnotherRestaurant_throwsForbidden() {
        Category foreignCategory = Category.builder()
                .id(UUID.randomUUID()).name("Custom").type("SALE")
                .restaurant(otherRestaurant).isDefault(false).build();
        SaleRequest request = new SaleRequest(new BigDecimal("300.00"), foreignCategory.getId(), null);
        when(categoryRepository.findById(foreignCategory.getId())).thenReturn(Optional.of(foreignCategory));

        assertThatThrownBy(() -> saleService.recordSale("owner@test.com", request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.FORBIDDEN));
    }

    // ── Business Rule 4: Finalized (CLOSED) sale cannot be edited ───────────────
    @Test
    @DisplayName("Rule 4 - updateSale() throws IllegalStateException for CLOSED sale")
    void updateSale_closedSale_throwsIllegalState() {
        UUID saleId = UUID.randomUUID();
        Sale closedSale = Sale.builder()
                .id(saleId).amount(new BigDecimal("100.00"))
                .category(saleCategory).restaurant(restaurant).status(SaleStatus.CLOSED).build();
        SaleRequest updateRequest = new SaleRequest(new BigDecimal("200.00"), saleCategory.getId(), "edit");

        when(saleRepository.findById(saleId)).thenReturn(Optional.of(closedSale));

        assertThatThrownBy(() -> saleService.updateSale("owner@test.com", saleId, updateRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("finalized");
    }

    // ── Business Rule 4: Finalized (CLOSED) sale cannot be deleted ──────────────
    @Test
    @DisplayName("Rule 4 - deleteSale() throws IllegalStateException for CLOSED sale")
    void deleteSale_closedSale_throwsIllegalState() {
        UUID saleId = UUID.randomUUID();
        Sale closedSale = Sale.builder()
                .id(saleId).amount(new BigDecimal("100.00"))
                .category(saleCategory).restaurant(restaurant).status(SaleStatus.CLOSED).build();

        when(saleRepository.findById(saleId)).thenReturn(Optional.of(closedSale));

        assertThatThrownBy(() -> saleService.deleteSale("owner@test.com", saleId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("finalized");
    }

    // ── Business Rule 2: Tenant Isolation — cannot access another restaurant's sale ──
    @Test
    @DisplayName("Rule 2 - deleteSale() blocks access to another restaurant's record")
    void deleteSale_saleFromOtherRestaurant_throwsSecurityException() {
        UUID saleId = UUID.randomUUID();
        Sale foreignSale = Sale.builder()
                .id(saleId).amount(new BigDecimal("150.00"))
                .category(saleCategory).restaurant(otherRestaurant).status(SaleStatus.OPEN).build();

        when(saleRepository.findById(saleId)).thenReturn(Optional.of(foreignSale));

        assertThatThrownBy(() -> saleService.deleteSale("owner@test.com", saleId))
                .isInstanceOf(SecurityException.class);
    }

    // ── Business Rule 4: OPEN sale can be hard-deleted ──────────────────────────
    @Test
    @DisplayName("Rule 4 - deleteSale() hard-deletes an OPEN sale")
    void deleteSale_openSale_isHardDeleted() {
        UUID saleId = UUID.randomUUID();
        Sale openSale = Sale.builder()
                .id(saleId).amount(new BigDecimal("250.00"))
                .category(saleCategory).restaurant(restaurant).status(SaleStatus.OPEN).build();

        when(saleRepository.findById(saleId)).thenReturn(Optional.of(openSale));

        saleService.deleteSale("owner@test.com", saleId);

        verify(saleRepository).delete(openSale);
    }

    // ── Business Rule 8: Restaurant must be set up before recording sales ────────
    @Test
    @DisplayName("Rule 8 - recordSale() throws when no restaurant profile exists")
    void recordSale_noRestaurantProfile_throwsException() {
        when(restaurantRepository.findByOwner(owner)).thenReturn(Optional.empty());
        SaleRequest request = new SaleRequest(new BigDecimal("100.00"), UUID.randomUUID(), null);

        assertThatThrownBy(() -> saleService.recordSale("owner@test.com", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Restaurant profile not found");
    }
}
