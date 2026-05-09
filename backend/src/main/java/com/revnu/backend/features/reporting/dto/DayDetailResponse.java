package com.revnu.backend.features.reporting.dto;

import java.util.List;

import com.revnu.backend.features.expenses.dto.ExpenseResponse;
import com.revnu.backend.features.sales.dto.SaleResponse;

public record DayDetailResponse(
        DailySummaryDto summary,
        List<SaleResponse> sales,
        List<ExpenseResponse> expenses,
        List<CashFlowRecord> outflow
        ) {

}
