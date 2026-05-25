package com.revnu.mobile.features.staff.model

import java.math.BigDecimal
import java.util.UUID

import com.google.gson.annotations.SerializedName

data class SalaryRequest(
    @SerializedName("staffId") val staffId: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("paymentDate") val paymentDate: String 
)
