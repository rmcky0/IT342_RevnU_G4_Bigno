package com.revnu.mobile.features.sales.model

data class SaleRequest(
    val amount: Double,
    val tagNames: List<String>? = emptyList(),
    val description: String
)