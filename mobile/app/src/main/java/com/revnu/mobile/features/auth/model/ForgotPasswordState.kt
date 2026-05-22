package com.revnu.mobile.features.auth.model

sealed class ForgotPasswordState {
    object Idle : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    data class OtpSent(val email: String) : ForgotPasswordState()
    data class OtpVerified(val email: String, val otp: String) : ForgotPasswordState()
    object PasswordReset : ForgotPasswordState()
    data class Error(val message: String) : ForgotPasswordState()
}
