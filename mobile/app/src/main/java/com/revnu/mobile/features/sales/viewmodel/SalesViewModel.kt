package com.revnu.mobile.features.sales.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revnu.mobile.core.network.RetrofitClient
import com.revnu.mobile.features.sales.model.CloseDayRequest
import com.revnu.mobile.features.sales.model.SaleRequest
import com.revnu.mobile.features.sales.model.SaleResponse
import com.revnu.mobile.features.sales.repository.SalesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SalesViewModel : ViewModel() {

    private val repository = SalesRepository()

    private val _sales = MutableStateFlow<List<SaleResponse>>(emptyList())
    val sales: StateFlow<List<SaleResponse>> = _sales

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    private val _isEodLocked = MutableStateFlow(false)
    val isEodLocked: StateFlow<Boolean> = _isEodLocked.asStateFlow()

    fun lockEod() {
        viewModelScope.launch {
            try {
                // Get today's date formatted perfectly for your backend
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val todayDateString = sdf.format(Date())

                val request = CloseDayRequest(date = todayDateString)
                val response = RetrofitClient.apiService.closeDay(request)

                if (response.isSuccessful) {
                    // Success! Lock the UI.
                    _isEodLocked.value = true
                } else {
                    _errorMessage.value = "Failed to lock EOD. Please try again."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            }
        }
    }

    fun deleteSale(saleId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteSale(saleId)
                if (response.isSuccessful) {
                    // Remove it from the local list so the UI updates instantly
                    val currentList = _sales.value.toMutableList()
                    currentList.removeAll { it.id == saleId }
                    _sales.value = currentList
                } else {
                    _errorMessage.value = "Failed to delete record."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            }
        }
    }
    fun loadSales() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.fetchSales()

            if (result.isSuccess) {
                _sales.value = result.getOrNull() ?: emptyList()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.localizedMessage
            }

            _isLoading.value = false
        }
    }

    fun addSale(amount: Double, description: String, tags: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true

            val request = SaleRequest(amount, tags, description)
            val result = repository.createSale(request)

            if (result.isSuccess) {
                // Reload the list to show the new sale
                loadSales()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.localizedMessage
                _isLoading.value = false
            }
        }
    }

    // Inside SalesViewModel.kt

    fun editSale(saleId: String, amount: Double, tags: List<String>,  notes: String,) {
        viewModelScope.launch {
            try {
                val request = SaleRequest(amount = amount, tagNames =tags, description = notes)

                val response = RetrofitClient.apiService.updateSale(saleId, request)

                if (response.isSuccessful) {
                    val updatedSale = response.body()?.data
                    if (updatedSale != null) {
                        val currentList = _sales.value.toMutableList()
                        val index = currentList.indexOfFirst { it.id == saleId }

                        if (index != -1) {
                            currentList[index] = updatedSale
                            _sales.value = currentList
                        }
                    } else {
                        loadSales()
                    }
                } else {
                    _errorMessage.value = "Failed to update record."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            }
        }
    }
}