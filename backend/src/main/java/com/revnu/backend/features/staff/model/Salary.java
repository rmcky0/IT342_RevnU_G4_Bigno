package com.revnu.backend.features.staff.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.revnu.backend.features.restaurants.model.Restaurant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "salaries")
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "staff_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_salaries_staff")
    )
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "restaurant_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_salaries_restaurant")
    )
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SalaryStatus status = SalaryStatus.OPEN;

    @Column(name = "report_date")
    private LocalDate reportDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    // --- Builder Pattern ---
    public static SalaryBuilder builder() {
        return new SalaryBuilder();
    }

    public static class SalaryBuilder {

        private UUID id;
        private BigDecimal amount;
        private LocalDate paymentDate;
        private Staff staff;
        private Restaurant restaurant;
        private SalaryStatus status;

        public SalaryBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public SalaryBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public SalaryBuilder paymentDate(LocalDate paymentDate) {
            this.paymentDate = paymentDate;
            return this;
        }

        public SalaryBuilder staff(Staff staff) {
            this.staff = staff;
            return this;
        }

        public SalaryBuilder restaurant(Restaurant restaurant) {
            this.restaurant = restaurant;
            return this;
        }

        public SalaryBuilder status(SalaryStatus status) {
            this.status = status;
            return this;
        }

        public Salary build() {
            Salary salary = new Salary();
            salary.id = this.id;
            salary.amount = this.amount;
            salary.paymentDate = this.paymentDate;
            salary.staff = this.staff;
            salary.restaurant = this.restaurant;
            salary.status = this.status != null ? this.status : SalaryStatus.OPEN;
            return salary;
        }
    }

    // --- Getters and Setters ---
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public SalaryStatus getStatus() {
        return status;
    }

    public void setStatus(SalaryStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
