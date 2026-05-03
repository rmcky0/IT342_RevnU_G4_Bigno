package com.revnu.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.revnu.backend.dto.ExpenseRequest;
import com.revnu.backend.dto.ExpenseResponse;
import com.revnu.backend.model.Expense;
import com.revnu.backend.model.User;
import com.revnu.backend.repository.ExpenseRepository;
import com.revnu.backend.repository.UserRepository;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public ExpenseResponse createExpense(String userEmail, ExpenseRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = new Expense(user, request.getExpenseDate(), request.getCategory(), request.getAmount(), request.getDescription(), request.getPaymentMethod());
        Expense saved = expenseRepository.save(expense);
        return toResponse(saved);
    }

    public List<ExpenseResponse> getExpensesByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return expenseRepository.findByUser(user).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ExpenseResponse> getExpensesByDateRange(String userEmail, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return expenseRepository.findByUserAndExpenseDateBetween(user, startDate, endDate).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ExpenseResponse> getExpensesByCategory(String userEmail, String category) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return expenseRepository.findByUserAndCategory(user, category).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ExpenseResponse getExpenseById(UUID id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense record not found"));
        return toResponse(expense);
    }

    public ExpenseResponse updateExpense(UUID id, ExpenseRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense record not found"));

        expense.setExpenseDate(request.getExpenseDate());
        expense.setCategory(request.getCategory());
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setPaymentMethod(request.getPaymentMethod());

        Expense updated = expenseRepository.save(expense);
        return toResponse(updated);
    }

    public void deleteExpense(UUID id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense record not found"));
        expenseRepository.delete(expense);
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(expense.getId(), expense.getExpenseDate(), expense.getCategory(), expense.getAmount(), expense.getDescription(), expense.getPaymentMethod(), expense.getCreatedAt(), expense.getUpdatedAt());
    }
}
