package com.revnu.mobile.features.analytics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revnu.mobile.features.analytics.model.OpenAnalyticsData
import com.revnu.mobile.features.analytics.repository.AnalyticsRepository
import kotlinx.coroutines.async
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
                val dailyDeferred   = async { repository.getDailyAnalytics() }
                val salesDeferred   = async { repository.getOpenSales() }
                val expensesDeferred = async { repository.getOpenExpenses() }
                val salariesDeferred = async { repository.getAllSalaries() }

                val dailyResponse = dailyDeferred.await()
                val openSales     = salesDeferred.await()
                val openExpenses  = expensesDeferred.await()
                val allSalaries   = salariesDeferred.await()

                val isClosed = dailyResponse.body()?.data?.isClosed ?: false

                val totalSales     = openSales.sumOf { it.amount }
                val totalExpenses  = openExpenses.sumOf { it.amount }
                val totalSalaries  = allSalaries.filter { it.status != "CLOSED" }.sumOf { it.amount }
                val netProfit      = totalSales - totalExpenses - totalSalaries

                _uiState.value = UiState.Success(
                    OpenAnalyticsData(
                        totalSales         = totalSales,
                        totalExpenses      = totalExpenses,
                        totalSalaries      = totalSalaries,
                        netProfit          = netProfit,
                        saleRecordsCount   = openSales.size.toLong(),
                        expenseRecordsCount = openExpenses.size.toLong(),
                        isClosed           = isClosed
                    )
                )
                lastFetchedAt = System.currentTimeMillis()
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
        data class Success(val analytics: OpenAnalyticsData) : UiState()
        data class Error(val message: String) : UiState()
    }
}
