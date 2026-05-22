package com.revnu.backend.features.categories.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.categories.dto.CategoryRequest;
import com.revnu.backend.features.categories.dto.CategoryResponse;
import com.revnu.backend.features.categories.model.Category;
import com.revnu.backend.features.categories.repository.CategoryRepository;
import com.revnu.backend.features.expenses.repository.ExpenseRepository;
import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;
import com.revnu.backend.features.sales.repository.SaleRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final SaleRepository saleRepository;
    private final ExpenseRepository expenseRepository;

    public CategoryService(CategoryRepository categoryRepository,
            UserRepository userRepository,
            RestaurantRepository restaurantRepository,
            SaleRepository saleRepository,
            ExpenseRepository expenseRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.saleRepository = saleRepository;
        this.expenseRepository = expenseRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories(String email, String type) {
        Restaurant restaurant = getRestaurant(email);
        List<Category> categories = (type != null && !type.isBlank())
                ? categoryRepository.findAllVisibleToRestaurantByType(restaurant, type.toUpperCase())
                : categoryRepository.findAllVisibleToRestaurant(restaurant);
        return categories.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse createCategory(String email, CategoryRequest request) {
        Restaurant restaurant = getRestaurant(email);
        Category category = Category.builder()
                .name(request.name().trim())
                .type(request.type().toUpperCase())
                .restaurant(restaurant)
                .isDefault(false)
                .build();
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(String email, UUID categoryId, CategoryRequest request) {
        Restaurant restaurant = getRestaurant(email);
        Category category = getOwnedCategory(categoryId, restaurant);

        if (category.isDefault()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Predefined categories cannot be renamed.");
        }

        category.setName(request.name().trim());
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(String email, UUID categoryId) {
        Restaurant restaurant = getRestaurant(email);
        Category category = getOwnedCategory(categoryId, restaurant);

        if (category.isDefault()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Predefined categories cannot be deleted.");
        }

        boolean inUseBySales = saleRepository.existsByCategory(category);
        boolean inUseByExpenses = expenseRepository.existsByCategory(category);

        if (inUseBySales || inUseByExpenses) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Category is in use by existing records and cannot be deleted.");
        }

        categoryRepository.delete(category);
    }

    private Category getOwnedCategory(UUID categoryId, Restaurant restaurant) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Category not found."));

        if (!category.isDefault() && !category.getRestaurant().getId().equals(restaurant.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You do not have permission to modify this category.");
        }
        return category;
    }

    private Restaurant getRestaurant(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        return restaurantRepository.findByOwner(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Restaurant profile not found."));
    }

    private CategoryResponse toResponse(Category c) {
        return new CategoryResponse(
                c.getId(),
                c.getName(),
                c.getType(),
                c.isDefault(),
                c.getRestaurant() != null ? c.getRestaurant().getId() : null
        );
    }
}
