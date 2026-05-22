package com.revnu.mobile.features.sales.model

import com.google.gson.annotations.SerializedName

data class SaleRequest(
    @SerializedName("amount") val amount: Double,
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("notes") val notes: String?
)