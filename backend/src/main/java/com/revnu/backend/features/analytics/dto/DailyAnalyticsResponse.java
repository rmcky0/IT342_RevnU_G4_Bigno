package com.revnu.backend.features.analytics.dto;

import java.math.BigDecimal;
import java.util.List;

public record DailyAnalyticsResponse(
        BigDecimal totalSales,
        BigDecimal totalExpenses,
        BigDecimal totalSalaries,
        BigDecimal netProfit,
        BigDecimal yesterdaySales,
        BigDecimal yesterdayExpenses,
        BigDecimal yesterdayProfit,
        long saleRecordsCount,
        long expenseRecordsCount,
        List<SalesTrendPoint> salesTrend,
        List<TagSlice> categories,
        String yesterdayLabel,
        String date,
        boolean isClosed
        ) {

}
