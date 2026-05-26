package com.revnu.backend.features.sales.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.revnu.backend.features.categories.model.Category;
import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.sales.model.Sale;
import com.revnu.backend.features.sales.model.SaleStatus;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID> {

    Page<Sale> findByRestaurant(Restaurant restaurant, Pageable pageable);

    Page<Sale> findByRestaurantAndStatus(Restaurant restaurant, SaleStatus status, Pageable pageable);

    List<Sale> findByRestaurantAndStatus(Restaurant restaurant, SaleStatus status);

    List<Sale> findByRestaurantAndCreatedAtBetween(Restaurant restaurant, LocalDateTime start, LocalDateTime end);

    List<Sale> findByRestaurantAndCreatedAtBetweenAndStatus(
            Restaurant restaurant,
            LocalDateTime start,
            LocalDateTime end,
            SaleStatus status
    );

    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM Sale s WHERE s.restaurant = :restaurant")
    BigDecimal sumSalesByRestaurant(@org.springframework.data.repository.query.Param("restaurant") Restaurant restaurant);

    boolean existsByCategory(Category category);
}
