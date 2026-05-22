package com.revnu.backend.features.analytics.dto;

import java.math.BigDecimal;

public record CategoryBreakdownItem(
        String name,
        BigDecimal total
) {
}
