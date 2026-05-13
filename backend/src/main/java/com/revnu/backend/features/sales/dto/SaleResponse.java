package com.revnu.backend.features.sales.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.revnu.backend.features.sales.model.SaleStatus;

public record SaleResponse(
        UUID id,
        BigDecimal amount,
        List<String> tags,
        String description,
        SaleStatus status,
        LocalDateTime createdAt
        ) {

}
