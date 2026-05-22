package com.revnu.backend.features.analytics.dto;

import java.math.BigDecimal;

public record CategorySlice(
        String name,
        BigDecimal value,
        String color
) {
}
