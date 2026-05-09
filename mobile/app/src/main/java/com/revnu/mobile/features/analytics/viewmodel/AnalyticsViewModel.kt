package com.revnu.mobile.features.analytics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revnu.mobile.features.analytics.model.DailyAnalyticsResponse
import com.revnu.mobile.features.analytics.repository.AnalyticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnalyticsViewModel(private val repository: AnalyticsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadAnalytics()
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = repository.getDailyAnalytics()

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!

                    // Since your backend wraps data in a "data" field
                    val data = apiResponse.data

                    if (data != null) {
                        _uiState.value = UiState.Success(data)
                    } else {
                        _uiState.value = UiState.Error("No data available for today.")
                    }
                } else {
                    _uiState.value = UiState.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Connection failed. Check your internet.")
            }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val analytics: DailyAnalyticsResponse) : UiState()
        data class Error(val message: String) : UiState()
    }
}