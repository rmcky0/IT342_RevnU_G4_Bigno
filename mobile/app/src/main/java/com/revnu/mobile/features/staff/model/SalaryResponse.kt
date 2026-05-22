package com.revnu.mobile.features.staff.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.math.BigDecimal



data class SalaryResponse(
    @SerializedName("id") val id: String,
    @SerializedName("staffId") val staffId: String,
    @SerializedName("staffName") val staffName: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("paymentDate") val paymentDate: String,
    @SerializedName("status") val status: String,
    @SerializedName("createdAt") val createdAt: String
)