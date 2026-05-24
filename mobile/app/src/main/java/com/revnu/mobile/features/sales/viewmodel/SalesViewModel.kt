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

    // 1. Expose the list of sales directly for the UI to observe
    private val _sales = MutableStateFlow<List<SaleResponse>>(emptyList())
    val sales: StateFlow<List<SaleResponse>> = _sales.asStateFlow()

    // 2. Expose the EOD lock state
    private val _isEodLocked = MutableStateFlow(false)
    val isEodLocked: StateFlow<Boolean> = _isEodLocked.asStateFlow()

    fun loadSales() {
        viewModelScope.launch {
            val result = repository.getTodaySales()

            result.fold(
                onSuccess = { salesList ->
                    _sales.value = salesList

                    // Note: If your backend returns the EOD status when you fetch sales,
                    // you can automatically update _isEodLocked.value here.
                },
                onFailure = { error ->
                    // For a production app, you might want to expose a SharedFlow to show error toasts.
                    // For now, it just fails silently or logs the error.
                    error.printStackTrace()
                }
            )
        }
    }

    fun deleteSale(id: String) {
        viewModelScope.launch {
            val result = repository.deleteSale(id)

            result.fold(
                onSuccess = {
                    loadSales()
                },
                onFailure = { error ->
                    error.printStackTrace()
                }
            )
        }
    }

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