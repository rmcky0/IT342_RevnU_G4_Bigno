package com.revnu.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.revnu.backend.model.Sales;
import com.revnu.backend.model.User;

@Repository
public interface SalesRepository extends JpaRepository<Sales, UUID> {
    List<Sales> findByUser(User user);
    
    List<Sales> findByUserAndSaleDateBetween(User user, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT s FROM Sales s WHERE s.user = :user AND CAST(s.saleDate AS date) = :date")
    List<Sales> findByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);
}
