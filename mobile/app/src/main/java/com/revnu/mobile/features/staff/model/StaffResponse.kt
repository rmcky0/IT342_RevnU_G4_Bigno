package com.revnu.mobile.features.staff.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.math.BigDecimal
data class StaffResponse(
    @SerializedName("id") val id: String,
    @SerializedName("fullname") val fullname: String,
    @SerializedName("position") val position: String,
    @SerializedName("salaryRate") val salaryRate: Double
)