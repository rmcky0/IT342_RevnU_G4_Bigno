package com.revnu.mobile.features.analytics.model

data class OpenAnalyticsData(
    val totalSales: Double,
    val totalExpenses: Double,
    val totalSalaries: Double,
    val netProfit: Double,
    val saleRecordsCount: Long,
    val expenseRecordsCount: Long,
    val isClosed: Boolean
)
