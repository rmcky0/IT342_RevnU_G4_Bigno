package com.revnu.mobile.features.sales.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revnu.mobile.features.sales.model.SaleResponse
import com.revnu.mobile.features.sales.repository.SalesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SalesViewModel(
    private val repository: SalesRepository
) : ViewModel() {

    private val _sales = MutableStateFlow<List<SaleResponse>>(emptyList())
    val sales: StateFlow<List<SaleResponse>> = _sales.asStateFlow()

    private val _isEodLocked = MutableStateFlow(false)
    val isEodLocked: StateFlow<Boolean> = _isEodLocked.asStateFlow()

    private var lastFetchedAt = 0L

    fun loadSales(force: Boolean = false) {
        val now = System.currentTimeMillis()
        if (!force && lastFetchedAt > 0 && (now - lastFetchedAt) < CACHE_TTL_MS && _sales.value.isNotEmpty()) return

        viewModelScope.launch {
            val result = repository.getTodaySales()
            result.fold(
                onSuccess = { salesList ->
                    _sales.value = salesList
                    lastFetchedAt = System.currentTimeMillis()
                },
                onFailure = { it.printStackTrace() }
            )
        }
    }

    fun forceRefresh() = loadSales(force = true)

    companion object {
        private const val CACHE_TTL_MS = 5 * 60 * 1000L
    }

    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError: StateFlow<String?> = _deleteError.asStateFlow()

    fun deleteSale(id: String) {
        viewModelScope.launch {
            repository.deleteSale(id).fold(
                onSuccess = { forceRefresh() },
                onFailure = { error -> _deleteError.value = error.message }
            )
        }
    }

    fun clearDeleteError() { _deleteError.value = null }

    fun lockEod() {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayDate = dateFormat.format(Date())

            val result = repository.closeDay(todayDate)

            result.fold(
                onSuccess = {
                    _isEodLocked.value = true
                },
                onFailure = { error ->
                    error.printStackTrace()
                }
            )
        }
    }
}