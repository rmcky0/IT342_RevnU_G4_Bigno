package com.revnu.backend.features.staff.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.staff.model.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {

    List<Staff> findByRestaurant(Restaurant restaurant);

    List<Staff> findByRestaurantAndActiveTrue(Restaurant restaurant);
}
