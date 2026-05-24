package com.revnu.backend.features.categories;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.categories.dto.CategoryRequest;
import com.revnu.backend.features.categories.dto.CategoryResponse;
import com.revnu.backend.features.categories.model.Category;
import com.revnu.backend.features.categories.repository.CategoryRepository;
import com.revnu.backend.features.categories.service.CategoryService;
import com.revnu.backend.features.expenses.repository.ExpenseRepository;
import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;
import com.revnu.backend.features.sales.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Unit Tests")
class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private UserRepository userRepository;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private SaleRepository saleRepository;
    @Mock private ExpenseRepository expenseRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User owner;
    private Restaurant restaurant;
    private Category predefinedCategory;
    private Category customCategory;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(UUID.randomUUID()).email("owner@test.com").build();
        restaurant = Restaurant.builder().id(UUID.randomUUID()).name("My Restaurant").owner(owner).build();

        predefinedCategory = Category.builder()
                .id(UUID.randomUUID()).name("Meals").type("SALE")
                .restaurant(null).isDefault(true).build();

        customCategory = Category.builder()
                .id(UUID.randomUUID()).name("Custom Sales").type("SALE")
                .restaurant(restaurant).isDefault(false).build();

        when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
        when(restaurantRepository.findByOwner(owner)).thenReturn(Optional.of(restaurant));
    }

    // ── Business Rule 3: Predefined categories cannot be renamed ─────────────────
    @Test
    @DisplayName("Rule 3 - updateCategory() throws FORBIDDEN for predefined categories")
    void updateCategory_predefinedCategory_throwsForbidden() {
        UUID catId = predefinedCategory.getId();
        when(categoryRepository.findById(catId)).thenReturn(Optional.of(predefinedCategory));

        CategoryRequest req = new CategoryRequest("New Name", "SALE");

        assertThatThrownBy(() -> categoryService.updateCategory("owner@test.com", catId, req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.FORBIDDEN));
    }

    // ── Business Rule 3: Predefined categories cannot be deleted ─────────────────
    @Test
    @DisplayName("Rule 3 - deleteCategory() throws FORBIDDEN for predefined categories")
    void deleteCategory_predefinedCategory_throwsForbidden() {
        UUID catId = predefinedCategory.getId();
        when(categoryRepository.findById(catId)).thenReturn(Optional.of(predefinedCategory));

        assertThatThrownBy(() -> categoryService.deleteCategory("owner@test.com", catId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.FORBIDDEN));
    }

    // ── Business Rule 3: In-use category cannot be deleted (409 Conflict) ────────
    @Test
    @DisplayName("Rule 3 - deleteCategory() throws CONFLICT when category is used by sales")
    void deleteCategory_inUseBySales_throwsConflict() {
        UUID catId = customCategory.getId();
        when(categoryRepository.findById(catId)).thenReturn(Optional.of(customCategory));
        when(saleRepository.existsByCategory(customCategory)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.deleteCategory("owner@test.com", catId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.CONFLICT));
    }

    // ── Business Rule 3: In-use by expenses also blocks deletion ─────────────────
    @Test
    @DisplayName("Rule 3 - deleteCategory() throws CONFLICT when category is used by expenses")
    void deleteCategory_inUseByExpenses_throwsConflict() {
        UUID catId = customCategory.getId();
        when(categoryRepository.findById(catId)).thenReturn(Optional.of(customCategory));
        when(saleRepository.existsByCategory(customCategory)).thenReturn(false);
        when(expenseRepository.existsByCategory(customCategory)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.deleteCategory("owner@test.com", catId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.CONFLICT));
    }

    // ── Business Rule 3: Custom category not in use can be deleted ───────────────
    @Test
    @DisplayName("Rule 3 - deleteCategory() succeeds for unused custom category")
    void deleteCategory_unusedCustom_success() {
        UUID catId = customCategory.getId();
        when(categoryRepository.findById(catId)).thenReturn(Optional.of(customCategory));
        when(saleRepository.existsByCategory(customCategory)).thenReturn(false);
        when(expenseRepository.existsByCategory(customCategory)).thenReturn(false);

        categoryService.deleteCategory("owner@test.com", catId);

        verify(categoryRepository).delete(customCategory);
    }

    // ── Business Rule 3: Custom categories belong to owner's restaurant ───────────
    @Test
    @DisplayName("Rule 2 - createCategory() assigns category to owner's restaurant")
    void createCategory_assignsToRestaurant() {
        CategoryRequest req = new CategoryRequest("Custom Category", "SALE");
        Category saved = Category.builder()
                .id(UUID.randomUUID()).name("Custom Category").type("SALE")
                .restaurant(restaurant).isDefault(false).build();
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryResponse response = categoryService.createCategory("owner@test.com", req);

        assertThat(response.restaurantId()).isEqualTo(restaurant.getId());
        assertThat(response.isDefault()).isFalse();
    }

    // ── Business Rule 3: Cannot edit another restaurant's custom category ─────────
    @Test
    @DisplayName("Rule 2 - updateCategory() throws FORBIDDEN for another restaurant's category")
    void updateCategory_categoryFromOtherRestaurant_throwsForbidden() {
        Restaurant otherRestaurant = Restaurant.builder().id(UUID.randomUUID()).name("Other").build();
        Category foreignCategory = Category.builder()
                .id(UUID.randomUUID()).name("Foreign").type("SALE")
                .restaurant(otherRestaurant).isDefault(false).build();
        UUID catId = foreignCategory.getId();

        when(categoryRepository.findById(catId)).thenReturn(Optional.of(foreignCategory));

        assertThatThrownBy(() -> categoryService.updateCategory("owner@test.com", catId, new CategoryRequest("X", "SALE")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.FORBIDDEN));
    }
}
