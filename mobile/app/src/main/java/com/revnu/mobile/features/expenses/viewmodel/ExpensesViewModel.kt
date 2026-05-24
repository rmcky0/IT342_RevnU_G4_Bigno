package com.revnu.mobile.features.expenses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revnu.mobile.features.expenses.model.ExpenseResponse
import com.revnu.mobile.features.expenses.repository.ExpensesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpensesViewModel(
    private val repository: ExpensesRepository
) : ViewModel() {

    private val _expenses = MutableStateFlow<List<ExpenseResponse>>(emptyList())
    val expenses: StateFlow<List<ExpenseResponse>> = _expenses.asStateFlow()

    private val _isEodLocked = MutableStateFlow(false)
    val isEodLocked: StateFlow<Boolean> = _isEodLocked.asStateFlow()

    private var lastFetchedAt = 0L

    fun loadExpenses(force: Boolean = false) {
        val now = System.currentTimeMillis()
        if (!force && lastFetchedAt > 0 && (now - lastFetchedAt) < CACHE_TTL_MS && _expenses.value.isNotEmpty()) return

        viewModelScope.launch {
            val result = repository.getTodayExpenses()
            result.fold(
                onSuccess = {
                    _expenses.value = it
                    lastFetchedAt = System.currentTimeMillis()
                },
                onFailure = { it.printStackTrace() }
            )
        }
    }

    fun forceRefresh() = loadExpenses(force = true)

    fun lockEod() {
        viewModelScope.launch {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            repository.closeDay(date).onSuccess { _isEodLocked.value = true }
        }
    }

    companion object {
        private const val CACHE_TTL_MS = 5 * 60 * 1000L
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch {
            val result = repository.deleteExpense(id)
            result.fold(
                onSuccess = { loadExpenses() },
                onFailure = { it.printStackTrace() }
            )
        }
    }

    fun uploadReceipt(id: String, filePart: MultipartBody.Part) {
        viewModelScope.launch {
            val result = repository.uploadReceipt(id, filePart)
            result.fold(
                onSuccess = { loadExpenses() }, // Refresh list to show attached receipt icon
                onFailure = { it.printStackTrace() }
            )
        }
    }
}