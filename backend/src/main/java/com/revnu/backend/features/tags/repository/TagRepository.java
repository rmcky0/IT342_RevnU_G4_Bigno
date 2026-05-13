package com.revnu.backend.features.tags.repository;

import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.tags.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    List<Tag> findByRestaurant(Restaurant restaurant);

    List<Tag> findByRestaurantAndType(Restaurant restaurant, String type);

    Optional<Tag> findByNameAndRestaurantIdAndType(String name, UUID restaurantId, String type);
}
