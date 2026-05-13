package com.revnu.backend.features.sales.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;
import com.revnu.backend.features.sales.dto.SaleRequest;
import com.revnu.backend.features.sales.dto.SaleResponse;
import com.revnu.backend.features.sales.model.Sale;
import com.revnu.backend.features.sales.model.SaleStatus;
import com.revnu.backend.features.sales.repository.SaleRepository;
import com.revnu.backend.features.tags.model.Tag;
import com.revnu.backend.features.tags.repository.TagRepository;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final TagRepository tagRepository;

    public SaleService(SaleRepository saleRepository,
            UserRepository userRepository,
            RestaurantRepository restaurantRepository,
            TagRepository tagRepository) {
        this.saleRepository = saleRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional
    public SaleResponse recordSale(String ownerEmail, SaleRequest request) {
        Restaurant restaurant = getRestaurant(getUser(ownerEmail));

        Set<Tag> tags = resolveTags(request.tagNames(), restaurant);

        Sale sale = Sale.builder()
                .amount(request.amount())
                .description(request.description())
                .tags(tags)
                .restaurant(restaurant)
                .status(SaleStatus.OPEN)
                .build();

        return mapToResponse(saleRepository.save(sale));
    }

    public Page<SaleResponse> getAllSales(String ownerEmail, int page, int size) {
        Restaurant restaurant = getRestaurant(getUser(ownerEmail));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return saleRepository.findByRestaurant(restaurant, pageable)
                .map(this::mapToResponse);
    }

    public List<SaleResponse> getSalesByDate(String ownerEmail, LocalDate date) {
        Restaurant restaurant = getRestaurant(getUser(ownerEmail));
        return saleRepository.findByRestaurantAndCreatedAtBetween(
                restaurant,
                date.atStartOfDay(),
                date.atTime(23, 59, 59)
        ).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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

    @Transactional
    public SaleResponse updateSale(String ownerEmail, UUID saleId, SaleRequest request) {
        Restaurant restaurant = getRestaurant(getUser(ownerEmail));

        Sale sale = getValidatedSale(saleId, restaurant);
        if (sale.getStatus() == SaleStatus.CLOSED) {
            throw new IllegalStateException("Cannot edit a finalized sale record.");
        }

        sale.setAmount(request.amount());
        sale.setDescription(request.description());

        sale.setTags(resolveTags(request.tagNames(), restaurant));

        return mapToResponse(saleRepository.save(sale));
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
                .orElseThrow(() -> new IllegalArgumentException("Sale not found"));

        if (!sale.getRestaurant().getId().equals(restaurant.getId())) {
            throw new SecurityException("Unauthorized to access this record.");
        }
        return sale;
    }

    private Set<Tag> resolveTags(List<String> tagNames, Restaurant restaurant) {
        return tagNames.stream()
                .map(name -> tagRepository.findByNameAndRestaurantIdAndType(
                name.toUpperCase().trim(), restaurant.getId(), "SALES")
                .orElseGet(() -> {
                    Tag newTag = Tag.builder()
                            .name(name.toUpperCase().trim())
                            .restaurant(restaurant)
                            .type("SALES")
                            .build();
                    return tagRepository.save(newTag);
                }))
                .collect(Collectors.toSet());
    }

    private SaleResponse mapToResponse(Sale sale) {
        return new SaleResponse(
                sale.getId(),
                sale.getAmount(),
                sale.getTags().stream().map(Tag::getName).collect(Collectors.toList()),
                sale.getDescription(),
                sale.getStatus(),
                sale.getCreatedAt()
        );
    }
}
