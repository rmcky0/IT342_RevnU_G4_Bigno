package com.revnu.backend.features.analytics.dto;

import java.math.BigDecimal;

public record HourlyExpensePoint(
        Integer hour,
        BigDecimal total
        ) {

}
