package com.revnu.mobile.features.sales.model

import com.google.gson.annotations.SerializedName

data class CloseDayRequest(
    @SerializedName("date") val date: String
)