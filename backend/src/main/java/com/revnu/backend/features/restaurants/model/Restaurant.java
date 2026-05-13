package com.revnu.backend.features.restaurants.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.files.model.FileRecord;
import com.revnu.backend.features.staff.model.Staff;
import com.revnu.backend.features.tags.model.Tag;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "physical_location", columnDefinition = "TEXT")
    private String physicalLocation;

    @Column(name = "opening_hrs")
    private LocalTime openingHrs;

    @Column(name = "closing_hrs")
    private LocalTime closingHrs;

    @Embedded
    private AppSettings appSettings = new AppSettings();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "owner_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_restaurants_owner")
    )
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "logo_file_id",
            foreignKey = @ForeignKey(name = "fk_restaurants_logo_file")
    )
    private FileRecord logoFile;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Staff> staffMembers = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static RestaurantBuilder builder() {
        return new RestaurantBuilder();
    }

    public static class RestaurantBuilder {

        private UUID id;
        private String name;
        private String physicalLocation;
        private LocalTime openingHrs;
        private LocalTime closingHrs;
        private AppSettings appSettings = new AppSettings();
        private User owner;
        private FileRecord logoFile;
        private List<Tag> tags = new ArrayList<>();
        private List<Staff> staffMembers = new ArrayList<>();

        public RestaurantBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public RestaurantBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RestaurantBuilder physicalLocation(String physicalLocation) {
            this.physicalLocation = physicalLocation;
            return this;
        }

        public RestaurantBuilder openingHrs(LocalTime openingHrs) {
            this.openingHrs = openingHrs;
            return this;
        }

        public RestaurantBuilder closingHrs(LocalTime closingHrs) {
            this.closingHrs = closingHrs;
            return this;
        }

        public RestaurantBuilder appSettings(AppSettings appSettings) {
            this.appSettings = appSettings;
            return this;
        }

        public RestaurantBuilder owner(User owner) {
            this.owner = owner;
            return this;
        }

        public RestaurantBuilder logoFile(FileRecord logoFile) {
            this.logoFile = logoFile;
            return this;
        }

        public RestaurantBuilder tags(List<Tag> tags) {
            this.tags = tags;
            return this;
        }

        public RestaurantBuilder staffMembers(List<Staff> staffMembers) {
            this.staffMembers = staffMembers;
            return this;
        }

        public Restaurant build() {
            Restaurant restaurant = new Restaurant();
            restaurant.id = this.id;
            restaurant.name = this.name;
            restaurant.physicalLocation = this.physicalLocation;
            restaurant.openingHrs = this.openingHrs;
            restaurant.closingHrs = this.closingHrs;
            restaurant.appSettings = this.appSettings;
            restaurant.owner = this.owner;
            restaurant.logoFile = this.logoFile;
            restaurant.tags = this.tags;
            restaurant.staffMembers = this.staffMembers;
            return restaurant;
        }
    }

    // --- Getters and Setters ---
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhysicalLocation() {
        return physicalLocation;
    }

    public void setPhysicalLocation(String physicalLocation) {
        this.physicalLocation = physicalLocation;
    }

    public LocalTime getOpeningHrs() {
        return openingHrs;
    }

    public void setOpeningHrs(LocalTime openingHrs) {
        this.openingHrs = openingHrs;
    }

    public LocalTime getClosingHrs() {
        return closingHrs;
    }

    public void setClosingHrs(LocalTime closingHrs) {
        this.closingHrs = closingHrs;
    }

    public AppSettings getAppSettings() {
        return appSettings;
    }

    public void setAppSettings(AppSettings appSettings) {
        this.appSettings = appSettings;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public FileRecord getLogoFile() {
        return logoFile;
    }

    public void setLogoFile(FileRecord logoFile) {
        this.logoFile = logoFile;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Staff> getStaffMembers() {
        return staffMembers;
    }

    public void setStaffMembers(List<Staff> staffMembers) {
        this.staffMembers = staffMembers;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
