package com.revnu.backend.features.expenses.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.revnu.backend.features.expenses.model.ExpenseStatus;

public record ExpenseResponse(
        UUID id,
        BigDecimal amount,
        UUID categoryId,
        String categoryName,
        String notes,
        UUID fileId,
        ExpenseStatus status,
        LocalDateTime createdAt
        ) {

}
