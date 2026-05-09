package com.revnu.backend.features.tags.model;

import java.util.UUID;

import com.revnu.backend.features.restaurants.model.Restaurant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "tags", uniqueConstraints = {
    @UniqueConstraint(name = "uq_tag_name_restaurant_type", columnNames = {"name", "restaurant_id", "type"})
})
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    public static TagBuilder builder() {
        return new TagBuilder();
    }

    public static class TagBuilder {

        private UUID id;
        private String name;
        private String type;
        private Restaurant restaurant;

        public TagBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public TagBuilder name(String name) {
            this.name = name;
            return this;
        }

        public TagBuilder type(String type) {
            this.type = type;
            return this;
        }

        public TagBuilder restaurant(Restaurant restaurant) {
            this.restaurant = restaurant;
            return this;
        }

        public Tag build() {
            Tag tag = new Tag();
            tag.id = this.id;
            tag.name = this.name;
            tag.type = this.type;
            tag.restaurant = this.restaurant;
            return tag;
        }
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
