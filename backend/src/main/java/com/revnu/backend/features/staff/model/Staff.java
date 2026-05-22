package com.revnu.backend.features.staff.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

@Entity
@Table(name = "staff")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "fullname", nullable = false, length = 255)
    private String fullname;

    @Column(nullable = false)
    private String position;

    @Column(name = "salary_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal salaryRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_staff_restaurant"))
    private Restaurant restaurant;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static StaffBuilder builder() {
        return new StaffBuilder();
    }

    public static class StaffBuilder {

        private UUID id;
        private String fullname;
        private String position;
        private BigDecimal salaryRate;
        private Restaurant restaurant;

        public StaffBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public StaffBuilder fullname(String fullname) {
            this.fullname = fullname;
            return this;
        }

        public StaffBuilder position(String position) {
            this.position = position;
            return this;
        }

        public StaffBuilder salaryRate(BigDecimal salaryRate) {
            this.salaryRate = salaryRate;
            return this;
        }

        public StaffBuilder restaurant(Restaurant restaurant) {
            this.restaurant = restaurant;
            return this;
        }

        public Staff build() {
            Staff staff = new Staff();
            staff.id = this.id;
            staff.fullname = this.fullname;
            staff.position = this.position;
            staff.salaryRate = this.salaryRate;
            staff.restaurant = this.restaurant;
            staff.active = true;
            return staff;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public BigDecimal getSalaryRate() {
        return salaryRate;
    }

    public void setSalaryRate(BigDecimal salaryRate) {
        this.salaryRate = salaryRate;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
