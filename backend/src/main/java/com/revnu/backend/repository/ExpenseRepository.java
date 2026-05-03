package com.revnu.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.revnu.backend.model.Expense;
import com.revnu.backend.model.User;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    List<Expense> findByUser(User user);
    
    List<Expense> findByUserAndExpenseDateBetween(User user, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT e FROM Expense e WHERE e.user = :user AND CAST(e.expenseDate AS date) = :date")
    List<Expense> findByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);
    
    List<Expense> findByUserAndCategory(User user, String category);
}
