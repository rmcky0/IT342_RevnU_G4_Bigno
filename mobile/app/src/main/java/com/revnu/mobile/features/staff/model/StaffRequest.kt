package com.revnu.mobile.features.staff.model

import com.google.gson.annotations.SerializedName

data class StaffRequest(
    @SerializedName("fullname") val fullname: String,
    @SerializedName("position") val position: String,
    @SerializedName("salaryRate") val salaryRate: Double
)

