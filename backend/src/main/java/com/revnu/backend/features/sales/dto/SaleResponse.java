package com.revnu.backend.features.sales.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.revnu.backend.features.sales.model.SaleStatus;

public record SaleResponse(
        UUID id,
        BigDecimal amount,
        UUID categoryId,
        String categoryName,
        String notes,
        SaleStatus status,
        LocalDateTime createdAt
        ) {

}
