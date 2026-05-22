package com.revnu.mobile.features.restaurant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revnu.mobile.features.restaurant.model.RestaurantSetupRequest
import com.revnu.mobile.features.restaurant.model.RestaurantSetupState
import com.revnu.mobile.features.restaurant.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RestaurantSetupViewModel(
    private val repository: RestaurantRepository
) : ViewModel() {

    private val _setupState = MutableStateFlow<RestaurantSetupState>(RestaurantSetupState.Idle)
    val setupState: StateFlow<RestaurantSetupState> = _setupState.asStateFlow()

    fun completeSetup(name: String, location: String, opening: String, closing: String) {
        _setupState.value = RestaurantSetupState.Loading

        viewModelScope.launch {
            val request = RestaurantSetupRequest(
                name = name,
                physicalLocation = location,
                openingHrs = opening,
                closingHrs = closing
            )

            val result = repository.setupRestaurant(request)

            result.fold(
                onSuccess = {
                    _setupState.value = RestaurantSetupState.Success
                },
                onFailure = { error ->
                    _setupState.value = RestaurantSetupState.Error(error.message ?: "Failed to save restaurant details")
                }
            )
        }
    }

    fun resetState() {
        _setupState.value = RestaurantSetupState.Idle
    }
}