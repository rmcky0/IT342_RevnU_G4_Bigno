package com.revnu.backend.features.reporting.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.revnu.backend.features.restaurants.model.Restaurant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "daily_summaries", uniqueConstraints = @UniqueConstraint(
        name = "uq_daily_summary_restaurant_date",
        columnNames = {"restaurant_id", "report_date"}
))
public class DailySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "total_sales", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalSales;

    @Column(name = "total_expenses", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalExpenses;

    @Column(name = "total_salaries", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalSalaries = BigDecimal.ZERO;

    @Column(name = "net_profit", nullable = false, precision = 12, scale = 2)
    private BigDecimal netProfit;

    @Column(name = "report_sent", nullable = false)
    private boolean reportSent = false;

    @CreationTimestamp
    @Column(name = "generated_at", updatable = false)
    private LocalDateTime generatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_daily_summary_restaurant"))
    private Restaurant restaurant;

    public static DailySummaryBuilder builder() {
        return new DailySummaryBuilder();
    }

    public static class DailySummaryBuilder {

        private UUID id;
        private LocalDate reportDate;
        private BigDecimal totalSales;
        private BigDecimal totalExpenses;
        private BigDecimal totalSalaries = BigDecimal.ZERO;
        private BigDecimal netProfit;
        private boolean reportSent = false;
        private Restaurant restaurant;

        public DailySummaryBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public DailySummaryBuilder reportDate(LocalDate reportDate) {
            this.reportDate = reportDate;
            return this;
        }

        public DailySummaryBuilder totalSales(BigDecimal totalSales) {
            this.totalSales = totalSales;
            return this;
        }

        public DailySummaryBuilder totalExpenses(BigDecimal totalExpenses) {
            this.totalExpenses = totalExpenses;
            return this;
        }

        public DailySummaryBuilder totalSalaries(BigDecimal totalSalaries) {
            this.totalSalaries = totalSalaries;
            return this;
        }

        public DailySummaryBuilder netProfit(BigDecimal netProfit) {
            this.netProfit = netProfit;
            return this;
        }

        public DailySummaryBuilder reportSent(boolean reportSent) {
            this.reportSent = reportSent;
            return this;
        }

        public DailySummaryBuilder restaurant(Restaurant restaurant) {
            this.restaurant = restaurant;
            return this;
        }

        public DailySummary build() {
            DailySummary summary = new DailySummary();
            summary.id = this.id;
            summary.reportDate = this.reportDate;
            summary.totalSales = this.totalSales;
            summary.totalExpenses = this.totalExpenses;
            summary.totalSalaries = this.totalSalaries;
            summary.netProfit = this.netProfit;
            summary.reportSent = this.reportSent;
            summary.restaurant = this.restaurant;
            return summary;
        }
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getTotalSalaries() {
        return totalSalaries;
    }

    public void setTotalSalaries(BigDecimal totalSalaries) {
        this.totalSalaries = totalSalaries;
    }

    public BigDecimal getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(BigDecimal netProfit) {
        this.netProfit = netProfit;
    }

    public boolean isReportSent() {
        return reportSent;
    }

    public void setReportSent(boolean reportSent) {
        this.reportSent = reportSent;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
