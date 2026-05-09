package com.revnu.mobile.features.expenses.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revnu.mobile.core.network.RetrofitClient
import com.revnu.mobile.features.sales.model.CloseDayRequest
import com.revnu.mobile.features.expenses.model.ExpenseRequest
import com.revnu.mobile.features.expenses.model.ExpenseResponse
import com.revnu.mobile.features.expenses.repository.ExpensesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ExpensesViewModel : ViewModel() {

    private val repository = ExpensesRepository()

    private val _expenses = MutableStateFlow<List<ExpenseResponse>>(emptyList())
    val expenses: StateFlow<List<ExpenseResponse>> = _expenses

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

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteExpense(expenseId)
                if (response.isSuccessful) {
                    // Remove it from the local list so the UI updates instantly
                    val currentList = _expenses.value.toMutableList()
                    currentList.removeAll { it.id == expenseId }
                    _expenses.value = currentList
                } else {
                    _errorMessage.value = "Failed to delete record."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            }
        }
    }
    fun loadExpenses() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.fetchExpenses()

            if (result.isSuccess) {
                _expenses.value = result.getOrNull() ?: emptyList()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.localizedMessage
            }

            _isLoading.value = false
        }
    }

    fun addExpense(context: Context, amount: Double,  tags: List<String>, description: String, selectedReceiptUri: Uri?) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val request = ExpenseRequest(amount = amount, tagNames = tags, description = description)
                val response = RetrofitClient.apiService.recordExpense(request) // Assuming you have this directly in ApiService now, or via repository

                if (response.isSuccessful) {
                    val newExpense = response.body()?.data

                    // Step 2: If we have an image and the creation succeeded, upload it
                    if (newExpense != null && selectedReceiptUri != null) {
                        uploadReceiptFile(context, newExpense.id, selectedReceiptUri)
                    } else {
                        loadExpenses() // Just reload if no image
                    }
                } else {
                    _errorMessage.value = "Failed to save record."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun uploadReceiptFile(context: Context, expenseId: String, uri: Uri) {
        try {
            // Convert the Uri to a byte array
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes != null) {
                // Create the multipart body
                val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                val multipartFile = MultipartBody.Part.createFormData("file", "receipt.jpg", requestBody)

                val uploadResponse = RetrofitClient.apiService.uploadReceipt(expenseId, multipartFile)

                if (uploadResponse.isSuccessful) {
                    // The backend returns the updated ExpenseResponse with the fileId
                    loadExpenses()
                } else {
                    _errorMessage.value = "Expense saved, but receipt upload failed."
                    loadExpenses()
                }
            }
        } catch (e: Exception) {
            _errorMessage.value = "File read error: ${e.message}"
            loadExpenses()
        }
    }
    fun editExpense(expenseId: String, amount: Double, tags: List<String>, notes: String, selectedReceiptUri: Uri?) {
        viewModelScope.launch {
            try {
                val request = ExpenseRequest(
                    amount = amount,
                    tagNames = tags,
                    description = notes
                )

                // Note: File upload logic for selectedReceiptUri would go here too if it changed

                val response = RetrofitClient.apiService.updateExpense(expenseId, request)

                if (response.isSuccessful) {
                    val updatedExpense = response.body()?.data
                    if (updatedExpense != null) {
                        val currentList = _expenses.value.toMutableList()
                        val index = currentList.indexOfFirst { it.id == expenseId }

                        if (index != -1) {
                            currentList[index] = updatedExpense
                            _expenses.value = currentList
                        }
                    } else {
                        loadExpenses()
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