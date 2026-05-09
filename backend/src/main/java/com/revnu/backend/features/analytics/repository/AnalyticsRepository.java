package com.revnu.backend.features.analytics.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.revnu.backend.features.analytics.dto.HourlyExpensePoint;
import com.revnu.backend.features.analytics.dto.HourlySalePoint;
import com.revnu.backend.features.analytics.dto.TagBreakdownItem;
import com.revnu.backend.features.expenses.model.ExpenseStatus;
import com.revnu.backend.features.reporting.model.DailySummary;
import com.revnu.backend.features.sales.model.Sale;
import com.revnu.backend.features.sales.model.SaleStatus;
import com.revnu.backend.features.staff.model.SalaryStatus;

@Repository
public interface AnalyticsRepository extends JpaRepository<Sale, UUID> {

    // SALES 
    @Query("""
        SELECT COALESCE(SUM(s.amount), 0)
        FROM Sale s
        WHERE s.restaurant.id = :restaurantId
          AND CAST(s.createdAt AS date) = :date
          AND s.status = :status
    """)
    BigDecimal sumSalesByRestaurantAndDate(
            @Param("restaurantId") UUID restaurantId,
            @Param("date") LocalDate date,
            @Param("status") SaleStatus status
    );

    @Query("""
        SELECT COUNT(s)
        FROM Sale s
        WHERE s.restaurant.id = :restaurantId
          AND CAST(s.createdAt AS date) = :date
          AND s.status = :status
    """)
    Long countSalesByRestaurantAndDate(
            @Param("restaurantId") UUID restaurantId,
            @Param("date") LocalDate date,
            @Param("status") SaleStatus status
    );

    @Query("""
        SELECT new com.revnu.backend.features.analytics.dto.HourlySalePoint(
            CAST(EXTRACT(HOUR FROM s.createdAt) AS integer),
            COALESCE(SUM(s.amount), 0)
        )
        FROM Sale s
        WHERE s.restaurant.id = :restaurantId
          AND CAST(s.createdAt AS date) = :date
          AND s.status = :status
        GROUP BY EXTRACT(HOUR FROM s.createdAt)
        ORDER BY EXTRACT(HOUR FROM s.createdAt)
    """)
    List<HourlySalePoint> hourlySalesByRestaurantAndDate(
            @Param("restaurantId") UUID restaurantId,
            @Param("date") LocalDate date,
            @Param("status") SaleStatus status
    );

    @Query("""
        SELECT new com.revnu.backend.features.analytics.dto.HourlyExpensePoint(
            CAST(EXTRACT(HOUR FROM e.createdAt) AS integer),
            COALESCE(SUM(e.amount), 0)
        )
        FROM Expense e
        WHERE e.restaurant.id = :restaurantId
          AND CAST(e.createdAt AS date) = :date
          AND e.status = :status
        GROUP BY EXTRACT(HOUR FROM e.createdAt)
        ORDER BY EXTRACT(HOUR FROM e.createdAt)
    """)
    List<HourlyExpensePoint> hourlyExpensesByRestaurantAndDate(
            @Param("restaurantId") UUID restaurantId,
            @Param("date") LocalDate date,
            @Param("status") ExpenseStatus status
    );

    @Query("""
        SELECT new com.revnu.backend.features.analytics.dto.TagBreakdownItem(
            t.name,
            COALESCE(SUM(s.amount), 0)
        )
        FROM Sale s
        JOIN s.tags t
        WHERE s.restaurant.id = :restaurantId
          AND CAST(s.createdAt AS date) = :date
          AND s.status = :status
        GROUP BY t.name
        ORDER BY SUM(s.amount) DESC
    """)
    List<TagBreakdownItem> salesByTagAndDate(
            @Param("restaurantId") UUID restaurantId,
            @Param("date") LocalDate date,
            @Param("status") SaleStatus status
    );

    // EXPENSES 
    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.restaurant.id = :restaurantId
          AND CAST(e.createdAt AS date) = :date
          AND e.status = :status
    """)
    BigDecimal sumExpensesByRestaurantAndDate(
            @Param("restaurantId") UUID restaurantId,
            @Param("date") LocalDate date,
            @Param("status") ExpenseStatus status
    );

    @Query("""
        SELECT COUNT(e)
        FROM Expense e
        WHERE e.restaurant.id = :restaurantId
          AND CAST(e.createdAt AS date) = :date
          AND e.status = :status
    """)
    Long countExpensesByRestaurantAndDate(
            @Param("restaurantId") UUID restaurantId,
            @Param("date") LocalDate date,
            @Param("status") ExpenseStatus status
    );

    //  SALARIES 
    @Query("""
        SELECT COALESCE(SUM(sal.amount), 0)
        FROM Salary sal
        WHERE sal.restaurant.id = :restaurantId
          AND sal.paymentDate = :date
          AND sal.status = :status
    """)
    BigDecimal sumSalariesByRestaurantAndDate(
            @Param("restaurantId") UUID restaurantId,
            @Param("date") LocalDate date,
            @Param("status") SalaryStatus status
    );

    //  DAILY SUMMARIES 
    @Query("""
        SELECT ds
        FROM DailySummary ds
        WHERE ds.restaurant.id = :restaurantId
          AND ds.reportDate <= :upToDate
        ORDER BY ds.reportDate DESC
    """)
    List<DailySummary> findRecentSummaries(
            @Param("restaurantId") UUID restaurantId,
            @Param("upToDate") LocalDate upToDate,
            Pageable pageable
    );
}
