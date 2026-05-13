package com.revnu.mobile.features.expenses.model

data class ExpenseRequest(
    val amount: Double,
    val tagNames: List<String>? = emptyList(),
    val description: String
)