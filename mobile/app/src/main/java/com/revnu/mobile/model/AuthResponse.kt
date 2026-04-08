package com.revnu.mobile.model

import com.google.gson.annotations.SerializedName
data class AuthResponse(
    @SerializedName("accessToken")
    val accessToken: String? = null,
    val email: String,
    val fullName: String,
    val role: String,
    val status: String? = null,
    val message: String? = null
)
