package com.revnu.mobile.features.expenses.model

import com.google.gson.annotations.SerializedName

data class ExpenseResponse(
    @SerializedName("id") val id: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("categoryName") val categoryName: String?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("fileId") val fileId: String?,
    @SerializedName("status") val status: String,
    @SerializedName("createdAt") val createdAt: String
)