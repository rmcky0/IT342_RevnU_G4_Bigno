package com.revnu.mobile.features.auth.model

import com.google.gson.annotations.SerializedName
data class AuthResponse(
    @SerializedName("message")
    val message: String?,

    @SerializedName("email")
    val email: String,

    @SerializedName("fullname")
    val fullname: String?,

    @SerializedName("role")
    val role: String,

    @SerializedName("status")
    val status: String?,

    @SerializedName("provider")
    val provider: String?,

    @SerializedName("hasRestaurant")
    val hasRestaurant: Boolean,

    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String
)