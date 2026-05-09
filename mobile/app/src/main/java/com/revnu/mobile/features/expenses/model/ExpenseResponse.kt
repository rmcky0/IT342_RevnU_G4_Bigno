package com.revnu.mobile.features.expenses.model

data class ExpenseResponse(
    val id: String,
    val amount: Double,
    val tags: List<String>?,
    val description: String?,
    val fileId: String?, // Added fileId
    val status: String,
    val createdAt: String
)