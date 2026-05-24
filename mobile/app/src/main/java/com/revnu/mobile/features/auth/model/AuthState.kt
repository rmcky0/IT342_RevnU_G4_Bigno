package com.revnu.mobile.features.auth.model

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val role: String, val hasRestaurant: Boolean) : AuthState()
    object AdminDetected : AuthState()
    data class Error(val message: String) : AuthState()
}