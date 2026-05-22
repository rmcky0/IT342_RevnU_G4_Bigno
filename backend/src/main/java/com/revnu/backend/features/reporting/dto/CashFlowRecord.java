package com.revnu.backend.features.reporting.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CashFlowRecord(
        UUID id,
        String type,
        BigDecimal amount,
        String notes,
        LocalDateTime timestamp
        ) {

}
