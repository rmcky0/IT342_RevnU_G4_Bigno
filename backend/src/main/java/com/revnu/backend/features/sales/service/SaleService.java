package com.revnu.backend.features.sales.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;

    public SaleService(SaleRepository saleRepository,
            UserRepository userRepository,
            RestaurantRepository restaurantRepository,
            CategoryRepository categoryRepository) {
        this.saleRepository = saleRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public SaleResponse recordSale(String ownerEmail, SaleRequest request) {
        Restaurant restaurant = getRestaurant(getUser(ownerEmail));
        Category category = resolveCategory(request.categoryId(), restaurant);

        Sale sale = Sale.builder()
                .amount(request.amount())
                .notes(request.notes())
                .category(category)
                .restaurant(restaurant)
                .status(SaleStatus.OPEN)
                .build();

        return mapToResponse(saleRepository.save(sale));
    }

    public Page<SaleResponse> getAllSales(String ownerEmail, int page, int size) {
        Restaurant restaurant = getRestaurant(getUser(ownerEmail));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return saleRepository.findByRestaurantAndStatus(restaurant, SaleStatus.OPEN, pageable).map(this::mapToResponse);
    }

    public List<SaleResponse> getSalesByDate(String ownerEmail, LocalDate date) {
        Restaurant restaurant = getRestaurant(getUser(ownerEmail));
        return saleRepository.findByRestaurantAndCreatedAtBetween(
                restaurant, date.atStartOfDay(), date.atTime(23, 59, 59))
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public SaleResponse updateSale(String ownerEmail, UUID saleId, SaleRequest request) {
        Restaurant restaurant = getRestaurant(getUser(ownerEmail));
        Sale sale = getValidatedSale(saleId, restaurant);

        if (sale.getStatus() == SaleStatus.CLOSED) {
            throw new IllegalStateException("Cannot edit a finalized sale record.");
        }

        sale.setAmount(request.amount());
        sale.setNotes(request.notes());
        sale.setCategory(resolveCategory(request.categoryId(), restaurant));

        return mapToResponse(saleRepository.save(sale));
    }

    @Transactional
    public void deleteSale(String ownerEmail, UUID saleId) {
        Restaurant restaurant = getRestaurant(getUser(ownerEmail));
        Sale sale = getValidatedSale(saleId, restaurant);

        if (sale.getStatus() == SaleStatus.CLOSED) {
            throw new IllegalStateException("Cannot delete a finalized sale record.");
        }

        saleRepository.delete(sale);
    }

    private Category resolveCategory(UUID categoryId, Restaurant restaurant) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Category not found."));

        boolean isOwned = category.getRestaurant() != null
                && category.getRestaurant().getId().equals(restaurant.getId());
        boolean isDefault = category.isDefault();

        if (!isDefault && !isOwned) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Category does not belong to this restaurant.");
        }
        if (!"SALE".equalsIgnoreCase(category.getType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Category type must be SALE for sale records.");
        }
        return category;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private Restaurant getRestaurant(User owner) {
        return restaurantRepository.findByOwner(owner)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant profile not found."));
    }

    private Sale getValidatedSale(UUID saleId, Restaurant restaurant) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("Sale not found."));
        if (!sale.getRestaurant().getId().equals(restaurant.getId())) {
            throw new SecurityException("Unauthorized to access this record.");
        }
        return sale;
    }

    private SaleResponse mapToResponse(Sale sale) {
        return new SaleResponse(
                sale.getId(),
                sale.getAmount(),
                sale.getCategory().getId(),
                sale.getCategory().getName(),
                sale.getNotes(),
                sale.getStatus(),
                sale.getCreatedAt()
        );
    }
}
