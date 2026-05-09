package com.revnu.backend.features.expenses.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ExpenseRequest(
        @NotNull(message = "Expense amount is required")
        @Positive(message = "Expense amount must be greater than zero")
        BigDecimal amount,
        @NotNull(message = "Tags list cannot be null (but can be empty)")
        List<String> tagNames,
        @Size(max = 1000, message = "Description is too long (max 1000 characters)")
        String description
        ) {

}
