package com.revnu.mobile.model

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String
)