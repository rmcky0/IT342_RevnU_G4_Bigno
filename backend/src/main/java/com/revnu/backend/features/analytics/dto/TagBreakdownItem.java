package com.revnu.backend.features.analytics.dto;

import java.math.BigDecimal;

public record TagBreakdownItem(
        String name,
        BigDecimal total
        ) {

}
