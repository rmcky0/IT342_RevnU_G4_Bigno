package com.revnu.backend.features.sales.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SaleRequest(
        @NotNull(message = "Sale amount is required")
        @Positive(message = "Sale amount must be greater than zero")
        BigDecimal amount,
        @NotNull(message = "Category is required")
        UUID categoryId,
        String notes
        ) {

}
