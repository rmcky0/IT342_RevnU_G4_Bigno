package com.revnu.backend.features.analytics.dto;

import java.math.BigDecimal;

public record ProfitTrendPoint(
        String date,
        BigDecimal totalSales,
        BigDecimal totalExpenses,
        BigDecimal netProfit
        ) {

}
