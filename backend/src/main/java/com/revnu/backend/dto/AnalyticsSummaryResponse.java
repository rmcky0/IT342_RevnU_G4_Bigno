package com.revnu.backend.dto;

import java.math.BigDecimal;

public class AnalyticsSummaryResponse {
    private BigDecimal totalSales;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;

    public AnalyticsSummaryResponse() {}

    public AnalyticsSummaryResponse(BigDecimal totalSales, BigDecimal totalExpenses, BigDecimal netProfit) {
        this.totalSales = totalSales;
        this.totalExpenses = totalExpenses;
        this.netProfit = netProfit;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(BigDecimal netProfit) {
        this.netProfit = netProfit;
    }
}
