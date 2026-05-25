package com.revnu.backend.features.categories.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.revnu.backend.features.categories.model.Category;
import com.revnu.backend.features.restaurants.model.Restaurant;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    @Query("""
        SELECT c FROM Category c
        WHERE (c.restaurant IS NULL OR c.restaurant = :restaurant)
        ORDER BY c.isDefault DESC, c.name ASC
    """)
    List<Category> findAllVisibleToRestaurant(@Param("restaurant") Restaurant restaurant);

    @Query("""
        SELECT c FROM Category c
        WHERE (c.restaurant IS NULL OR c.restaurant = :restaurant)
          AND c.type = :type
        ORDER BY c.isDefault DESC, c.name ASC
    """)
    List<Category> findAllVisibleToRestaurantByType(
            @Param("restaurant") Restaurant restaurant,
            @Param("type") String type);

    List<Category> findByRestaurantIsNull();

    List<Category> findByRestaurantIsNullAndType(String type);

    boolean existsByNameAndTypeAndRestaurantIsNull(String name, String type);

    Optional<Category> findByIdAndRestaurant(UUID id, Restaurant restaurant);
}
