package com.revnu.mobile.features.auth.model

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String
)
