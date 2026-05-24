package com.revnu.mobile.features.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revnu.mobile.features.auth.model.AuthState
import com.revnu.mobile.features.auth.model.LoginRequest
import com.revnu.mobile.features.auth.model.RegisterRequest
import com.revnu.mobile.features.auth.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(request: LoginRequest) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = repository.login(request)

            result.fold(
                onSuccess = { response ->
                    if (response.role == "ADMIN") {
                        repository.logout()
                        _authState.value = AuthState.AdminDetected
                    } else {
                        // pass hasRestaurant here
                        _authState.value = AuthState.Success(response.role, response.hasRestaurant)
                    }
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "network error")
                }
            )
        }
    }

    fun register(request: RegisterRequest) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = repository.register(request)

            result.fold(
                onSuccess = { response ->
                    _authState.value = AuthState.Success(response.role, response.hasRestaurant)
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "registration failed")
                }
            )
        }
    }

    fun loginWithGoogle(idToken: String) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = repository.loginWithGoogle(idToken)

            result.fold(
                onSuccess = { response ->
                    if (response.role == "ADMIN") {
                        repository.logout()
                        _authState.value = AuthState.AdminDetected
                    } else {
                        _authState.value = AuthState.Success(response.role, response.hasRestaurant)
                    }
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "Google sign-in failed")
                }
            )
        }
    }

    fun logout() {
        repository.logout()
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}