package com.revnu.mobile.features.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revnu.mobile.features.auth.model.ForgotPasswordState
import com.revnu.mobile.features.auth.repository.ForgotPasswordRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val repository: ForgotPasswordRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val state: StateFlow<ForgotPasswordState> = _state.asStateFlow()

    private val _cooldown = MutableStateFlow(0)
    val cooldown: StateFlow<Int> = _cooldown.asStateFlow()

    private var cooldownJob: Job? = null

    fun requestOtp(email: String) {
        if (email.isBlank()) {
            _state.value = ForgotPasswordState.Error("Please enter your email address.")
            return
        }
        _state.value = ForgotPasswordState.Loading
        viewModelScope.launch {
            repository.requestOtp(email).fold(
                onSuccess = {
                    _state.value = ForgotPasswordState.OtpSent(email)
                    startCooldown()
                },
                onFailure = { _state.value = ForgotPasswordState.Error(it.message ?: "Failed to send OTP.") }
            )
        }
    }

    fun resendOtp(email: String) {
        if (_cooldown.value > 0) return
        _state.value = ForgotPasswordState.Loading
        viewModelScope.launch {
            repository.requestOtp(email).fold(
                onSuccess = {
                    _state.value = ForgotPasswordState.OtpSent(email)
                    startCooldown()
                },
                onFailure = { _state.value = ForgotPasswordState.Error(it.message ?: "Failed to resend OTP.") }
            )
        }
    }

    fun verifyOtp(email: String, otp: String) {
        if (otp.length != 6) {
            _state.value = ForgotPasswordState.Error("Enter the 6-digit OTP code.")
            return
        }
        _state.value = ForgotPasswordState.Loading
        viewModelScope.launch {
            repository.verifyOtp(email, otp).fold(
                onSuccess = { _state.value = ForgotPasswordState.OtpVerified(email, otp) },
                onFailure = { _state.value = ForgotPasswordState.Error(it.message ?: "Incorrect or expired OTP.") }
            )
        }
    }

    fun resetPassword(email: String, otp: String, newPassword: String, confirmPassword: String) {
        if (newPassword.length < 8) {
            _state.value = ForgotPasswordState.Error("Password must be at least 8 characters.")
            return
        }
        if (newPassword != confirmPassword) {
            _state.value = ForgotPasswordState.Error("Passwords do not match.")
            return
        }
        _state.value = ForgotPasswordState.Loading
        viewModelScope.launch {
            repository.resetPassword(email, otp, newPassword).fold(
                onSuccess = { _state.value = ForgotPasswordState.PasswordReset },
                onFailure = { _state.value = ForgotPasswordState.Error(it.message ?: "Failed to reset password.") }
            )
        }
    }

    fun clearError() {
        if (_state.value is ForgotPasswordState.Error) {
            _state.value = ForgotPasswordState.Idle
        }
    }

    private fun startCooldown() {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            for (i in 60 downTo 1) {
                _cooldown.value = i
                delay(1000)
            }
            _cooldown.value = 0
        }
    }

    override fun onCleared() {
        super.onCleared()
        cooldownJob?.cancel()
    }
}
