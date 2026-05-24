package com.revnu.mobile.features.sales.model

import com.google.gson.annotations.SerializedName

data class SaleResponse(
    @SerializedName("id") val id: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("categoryName") val categoryName: String?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("status") val status: String,
    @SerializedName("createdAt") val createdAt: String
)