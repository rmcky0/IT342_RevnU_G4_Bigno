package com.revnu.mobile.features.expenses.model

import com.google.gson.annotations.SerializedName

data class ExpenseRequest(
    @SerializedName("amount") val amount: Double,
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("notes") val notes: String?
)