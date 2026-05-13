package com.revnu.backend.features.reporting.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.revnu.backend.features.reporting.model.DailySummary;
import com.revnu.backend.features.restaurants.model.Restaurant;

@Repository
public interface DailySummaryRepository extends JpaRepository<DailySummary, UUID> {

    Optional<DailySummary> findByRestaurantAndReportDate(Restaurant restaurant, LocalDate reportDate);

    boolean existsByRestaurantAndReportDate(Restaurant restaurant, LocalDate reportDate);

    @Query("SELECT ds FROM DailySummary ds WHERE ds.restaurant.id = :restaurantId AND ds.reportDate <= :upToDate ORDER BY ds.reportDate DESC")
    List<DailySummary> findRecentByRestaurantId(
            @Param("restaurantId") UUID restaurantId,
            @Param("upToDate") LocalDate upToDate,
            Pageable pageable
    );

    List<DailySummary> findByRestaurantOrderByReportDateDesc(Restaurant restaurant);
}
