package com.revnu.backend.features.restaurants.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.restaurants.model.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    Optional<Restaurant> findByOwner(User owner);

    boolean existsByOwner(User owner);

    Optional<Restaurant> findByOwner_Id(UUID ownerId);

    Page<Restaurant> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Restaurant> findByOwnerIn(List<User> owners);
}
