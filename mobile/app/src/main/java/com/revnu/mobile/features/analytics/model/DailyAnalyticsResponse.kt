package com.revnu.mobile.features.analytics.model

data class DailyAnalyticsResponse(
    val totalSales: Double,
    val totalExpenses: Double,
    val totalSalaries: Double,
    val netProfit: Double,
    val yesterdaySales: Double,
    val yesterdayExpenses: Double,
    val yesterdayProfit: Double,
    val saleRecordsCount: Long,
    val expenseRecordsCount: Long,
    val isClosed: Boolean,
    val date: String
)