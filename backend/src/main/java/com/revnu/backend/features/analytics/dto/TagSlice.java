package com.revnu.backend.features.analytics.dto;

import java.math.BigDecimal;

public record TagSlice(
        String name,
        BigDecimal value,
        String color
        ) {

}
