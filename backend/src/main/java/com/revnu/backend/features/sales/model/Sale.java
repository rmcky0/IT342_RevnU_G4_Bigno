package com.revnu.backend.features.sales.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.tags.model.Tag;

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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "sale_tags",
            joinColumns = @JoinColumn(name = "sale_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            foreignKey = @ForeignKey(name = "fk_sales_to_tags"),
            inverseForeignKey = @ForeignKey(name = "fk_tags_to_sales")
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "restaurant_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_sales_restaurant")
    )
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SaleStatus status = SaleStatus.OPEN;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // --- Builder Pattern ---
    public static SaleBuilder builder() {
        return new SaleBuilder();
    }

    public static class SaleBuilder {

        private UUID id;
        private BigDecimal amount;
        private String description;
        private Set<Tag> tags = new HashSet<>();
        private Restaurant restaurant;
        private SaleStatus status = SaleStatus.OPEN;

        public SaleBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public SaleBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public SaleBuilder description(String description) {
            this.description = description;
            return this;
        }

        public SaleBuilder tags(Set<Tag> tags) {
            this.tags = tags;
            return this;
        }

        public SaleBuilder restaurant(Restaurant restaurant) {
            this.restaurant = restaurant;
            return this;
        }

        public SaleBuilder status(SaleStatus status) {
            this.status = status;
            return this;
        }

        public Sale build() {
            Sale sale = new Sale();
            sale.id = this.id;
            sale.amount = this.amount;
            sale.description = this.description;
            sale.tags = this.tags;
            sale.restaurant = this.restaurant;
            sale.status = this.status;
            return sale;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public SaleStatus getStatus() {
        return status;
    }

    public void setStatus(SaleStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
