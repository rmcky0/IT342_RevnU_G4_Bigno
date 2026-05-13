package com.revnu.backend.features.analytics.dto;

import java.math.BigDecimal;

public record SalesTrendPoint(
        String time,
        BigDecimal todaySales,
        BigDecimal todayExpenses,
        BigDecimal yesterdaySales,
        BigDecimal yesterdayExpenses
        ) {

}
