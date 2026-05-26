package com.revnu.backend.features.expenses.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.revnu.backend.features.categories.model.Category;
import com.revnu.backend.features.expenses.model.Expense;
import com.revnu.backend.features.expenses.model.ExpenseStatus;
import com.revnu.backend.features.restaurants.model.Restaurant;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    Page<Expense> findByRestaurant(Restaurant restaurant, Pageable pageable);

    Page<Expense> findByRestaurantAndStatus(Restaurant restaurant, ExpenseStatus status, Pageable pageable);

    List<Expense> findByRestaurantAndStatus(Restaurant restaurant, ExpenseStatus status);

    List<Expense> findByRestaurantAndReportDate(Restaurant restaurant, LocalDate reportDate);

    List<Expense> findByRestaurantAndCreatedAtBetween(Restaurant restaurant, LocalDateTime start, LocalDateTime end);

    List<Expense> findByRestaurantAndCreatedAtBetweenAndStatus(
            Restaurant restaurant,
            LocalDateTime start,
            LocalDateTime end,
            ExpenseStatus status
    );

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.restaurant = :restaurant")
    BigDecimal sumExpensesByRestaurant(@Param("restaurant") Restaurant restaurant);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.restaurant = :restaurant AND e.createdAt BETWEEN :start AND :end")
    BigDecimal sumExpensesByRestaurantAndDateRange(
            @Param("restaurant") Restaurant restaurant,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    boolean existsByCategory(Category category);
}
