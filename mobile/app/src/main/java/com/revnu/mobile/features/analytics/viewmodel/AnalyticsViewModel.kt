package com.revnu.mobile.features.analytics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revnu.mobile.features.analytics.model.DailyAnalyticsResponse
import com.revnu.mobile.features.analytics.repository.AnalyticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnalyticsViewModel(private val repository: AnalyticsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var lastFetchedAt = 0L

    init {
        loadAnalytics()
    }

    fun loadAnalytics(force: Boolean = false) {
        val now = System.currentTimeMillis()
        if (!force && lastFetchedAt > 0 && (now - lastFetchedAt) < CACHE_TTL_MS && _uiState.value is UiState.Success) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = repository.getDailyAnalytics()

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!.data
                    if (data != null) {
                        _uiState.value = UiState.Success(data)
                        lastFetchedAt = System.currentTimeMillis()
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

    fun forceRefresh() = loadAnalytics(force = true)

    fun lockEod() {
        viewModelScope.launch {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            repository.closeDay(date).onSuccess {
                forceRefresh()
            }
        }
    }

    companion object {
        private const val CACHE_TTL_MS = 5 * 60 * 1000L
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val analytics: DailyAnalyticsResponse) : UiState()
        data class Error(val message: String) : UiState()
    }
}