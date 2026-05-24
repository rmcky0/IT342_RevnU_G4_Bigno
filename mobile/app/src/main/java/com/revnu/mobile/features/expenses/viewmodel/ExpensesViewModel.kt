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

class ExpensesViewModel(
    private val repository: ExpensesRepository
) : ViewModel() {

    private val _expenses = MutableStateFlow<List<ExpenseResponse>>(emptyList())
    val expenses: StateFlow<List<ExpenseResponse>> = _expenses.asStateFlow()

    fun loadExpenses() {
        viewModelScope.launch {
            val result = repository.getTodayExpenses()
            result.fold(
                onSuccess = { _expenses.value = it },
                onFailure = { it.printStackTrace() }
            )
        }
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