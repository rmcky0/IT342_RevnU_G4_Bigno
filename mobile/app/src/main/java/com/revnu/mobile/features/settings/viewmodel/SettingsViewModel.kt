package com.revnu.mobile.features.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revnu.mobile.features.settings.model.ChangePasswordRequest
import com.revnu.mobile.features.settings.model.RestaurantProfileRequest
import com.revnu.mobile.features.settings.model.RestaurantProfileResponse
import com.revnu.mobile.features.settings.model.UpdateProfileRequest
import com.revnu.mobile.features.settings.model.UserProfileResponse
import com.revnu.mobile.features.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

sealed class SettingsState {
    object Idle : SettingsState()
    object Loading : SettingsState()
    data class Success(val message: String) : SettingsState()
    data class Error(val message: String) : SettingsState()
}

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfileResponse?>(null)
    val userProfile: StateFlow<UserProfileResponse?> = _userProfile.asStateFlow()

    private val _restaurantProfile = MutableStateFlow<RestaurantProfileResponse?>(null)
    val restaurantProfile: StateFlow<RestaurantProfileResponse?> = _restaurantProfile.asStateFlow()

    private val _state = MutableStateFlow<SettingsState>(SettingsState.Idle)
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            repository.getProfile().onSuccess { _userProfile.value = it }
        }
    }

    fun saveProfile(fullname: String) {
        viewModelScope.launch {
            _state.value = SettingsState.Loading
            repository.updateProfile(UpdateProfileRequest(fullname))
                .onSuccess {
                    _userProfile.value = it
                    _state.value = SettingsState.Success("Profile updated successfully.")
                }
                .onFailure { _state.value = SettingsState.Error(it.message ?: "Failed to update profile") }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _state.value = SettingsState.Loading
            repository.changePassword(ChangePasswordRequest(currentPassword, newPassword))
                .onSuccess { _state.value = SettingsState.Success("Password updated successfully.") }
                .onFailure { _state.value = SettingsState.Error(it.message ?: "Failed to change password") }
        }
    }

    fun loadRestaurantProfile() {
        viewModelScope.launch {
            repository.getRestaurantProfile().onSuccess { _restaurantProfile.value = it }
        }
    }

    fun saveRestaurantProfile(request: RestaurantProfileRequest) {
        viewModelScope.launch {
            _state.value = SettingsState.Loading
            repository.updateRestaurantProfile(request)
                .onSuccess {
                    _restaurantProfile.value = it
                    _state.value = SettingsState.Success("Restaurant profile updated.")
                }
                .onFailure { _state.value = SettingsState.Error(it.message ?: "Failed to update restaurant profile") }
        }
    }

    fun uploadLogo(filePart: MultipartBody.Part) {
        viewModelScope.launch {
            _state.value = SettingsState.Loading
            repository.updateRestaurantLogo(filePart)
                .onSuccess {
                    _state.value = SettingsState.Success("Logo updated successfully.")
                    loadRestaurantProfile()
                }
                .onFailure { _state.value = SettingsState.Error(it.message ?: "Failed to upload logo") }
        }
    }

    fun resetState() { _state.value = SettingsState.Idle }
}
