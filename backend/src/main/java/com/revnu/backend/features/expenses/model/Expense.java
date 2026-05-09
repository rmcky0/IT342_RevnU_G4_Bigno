package com.revnu.backend.features.expenses.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.revnu.backend.features.files.model.FileRecord;
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
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "expense_tags",
            joinColumns = @JoinColumn(name = "expense_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            foreignKey = @ForeignKey(name = "fk_expenses_to_tags"),
            inverseForeignKey = @ForeignKey(name = "fk_tags_to_expenses")
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "restaurant_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_expenses_restaurant")
    )
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "file_id",
            foreignKey = @ForeignKey(name = "fk_expenses_file")
    )
    private FileRecord file;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExpenseStatus status = ExpenseStatus.OPEN;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public static ExpenseBuilder builder() {
        return new ExpenseBuilder();
    }

    public static class ExpenseBuilder {

        private UUID id;
        private BigDecimal amount;
        private String description;
        private Set<Tag> tags = new HashSet<>();
        private Restaurant restaurant;
        private FileRecord file;
        private ExpenseStatus status = ExpenseStatus.OPEN;

        public ExpenseBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public ExpenseBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public ExpenseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ExpenseBuilder tags(Set<Tag> tags) {
            this.tags = tags;
            return this;
        }

        public ExpenseBuilder restaurant(Restaurant restaurant) {
            this.restaurant = restaurant;
            return this;
        }

        public ExpenseBuilder file(FileRecord file) {
            this.file = file;
            return this;
        }

        public ExpenseBuilder status(ExpenseStatus status) {
            this.status = status;
            return this;
        }

        public Expense build() {
            Expense expense = new Expense();
            expense.id = this.id;
            expense.amount = this.amount;
            expense.description = this.description;
            expense.tags = this.tags;
            expense.restaurant = this.restaurant;
            expense.file = this.file;
            expense.status = this.status;
            return expense;
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

    public FileRecord getFile() {
        return file;
    }

    public void setFile(FileRecord file) {
        this.file = file;
    }

    public ExpenseStatus getStatus() {
        return status;
    }

    public void setStatus(ExpenseStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
