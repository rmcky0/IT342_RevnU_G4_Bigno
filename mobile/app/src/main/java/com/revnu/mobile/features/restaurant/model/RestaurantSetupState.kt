package com.revnu.mobile.features.restaurant.model


sealed class RestaurantSetupState {
    object Idle : RestaurantSetupState()
    object Loading : RestaurantSetupState()
    object Success : RestaurantSetupState()
    data class Error(val message: String) : RestaurantSetupState()
}