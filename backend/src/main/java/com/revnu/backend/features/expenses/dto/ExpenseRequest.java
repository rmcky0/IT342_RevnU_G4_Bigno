package com.revnu.backend.features.expenses.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ExpenseRequest(
        @NotNull(message = "Expense amount is required")
        @Positive(message = "Expense amount must be greater than zero")
        BigDecimal amount,
        @NotNull(message = "Category is required")
        UUID categoryId,
        @Size(max = 1000, message = "Notes is too long (max 1000 characters)")
        String notes
        ) {

}
