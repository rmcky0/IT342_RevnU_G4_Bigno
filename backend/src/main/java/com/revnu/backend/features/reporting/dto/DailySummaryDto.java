package com.revnu.backend.features.reporting.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailySummaryDto(
        LocalDate date,
        BigDecimal totalSales,
        BigDecimal totalExpenses,
        BigDecimal totalSalaries,
        BigDecimal netProfit,
        boolean reportSent
        ) {

}
