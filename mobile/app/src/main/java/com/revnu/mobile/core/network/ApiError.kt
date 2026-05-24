package com.revnu.mobile.core.network

import com.google.gson.annotations.SerializedName

data class ApiError(
    @SerializedName("code") val code: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("details") val details: Any?
)