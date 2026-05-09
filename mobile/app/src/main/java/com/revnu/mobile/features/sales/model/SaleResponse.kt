package com.revnu.mobile.features.sales.model

import com.google.gson.annotations.SerializedName

data class SaleResponse(
    val id: String,
    val amount: Double,
    @SerializedName("tags") val tags: List<String>?,
    val description: String?,
    val status: String,
    val createdAt: String
)
