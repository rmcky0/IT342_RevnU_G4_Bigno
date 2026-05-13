package com.revnu.backend.features.staff.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.staff.model.Salary;
import com.revnu.backend.features.staff.model.SalaryStatus;
import com.revnu.backend.features.staff.model.Staff;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, UUID> {

    List<Salary> findByRestaurantOrderByCreatedAtDesc(Restaurant restaurant);

    List<Salary> findByRestaurantAndPaymentDate(Restaurant restaurant, LocalDate paymentDate);

    List<Salary> findByRestaurantAndPaymentDateAndStatus(
            Restaurant restaurant,
            LocalDate paymentDate,
            SalaryStatus status
    );

    Page<Salary> findByRestaurant(Restaurant restaurant, Pageable pageable);

    List<Salary> findByStaff(Staff staff);
}
