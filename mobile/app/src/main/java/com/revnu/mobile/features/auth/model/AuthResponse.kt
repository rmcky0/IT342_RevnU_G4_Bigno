package com.revnu.mobile.features.auth.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("email")
    val email: String,

    @SerializedName("fullname")
    val fullName: String,

    @SerializedName("role")
    val role: String,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("provider")
    val provider: String? = null,

    @SerializedName("hasRestaurant")
    val hasRestaurant: Boolean,

    @SerializedName("accessToken")
    val accessToken: String? = null
)