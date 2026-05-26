package com.revnu.mobile.features.entry.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revnu.mobile.features.entry.model.AddRecordState
import com.revnu.mobile.features.expenses.model.ExpenseRequest
import com.revnu.mobile.features.expenses.repository.ExpensesRepository
import okhttp3.MultipartBody
import com.revnu.mobile.features.sales.model.SaleRequest
import com.revnu.mobile.features.sales.repository.SalesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddRecordViewModel(
    private val salesRepository: SalesRepository,
    private val expensesRepository: ExpensesRepository
) : ViewModel() {

    private val _recordState = MutableStateFlow<AddRecordState>(AddRecordState.Idle)
    val recordState: StateFlow<AddRecordState> = _recordState.asStateFlow()

    fun saveRecord(
        isSaleMode: Boolean,
        amount: Double,
        notes: String,
        categoryId: String,
        recordId: String? = null,
        receiptPart: MultipartBody.Part? = null
    ) {
        _recordState.value = AddRecordState.Loading

        viewModelScope.launch {
            if (isSaleMode) {
                val request = SaleRequest(amount = amount, notes = notes, categoryId = categoryId)

                val result = if (recordId != null) {
                    salesRepository.updateSale(recordId, request)
                } else {
                    salesRepository.recordSale(request)
                }

                result.fold(
                    onSuccess = { _recordState.value = AddRecordState.Success("sale") },
                    onFailure = { error -> _recordState.value = AddRecordState.Error(error.message ?: "Failed to save sale") }
                )
            } else {
                val request = ExpenseRequest(amount = amount, notes = notes, categoryId = categoryId)

                val result = if (recordId != null) {
                    expensesRepository.updateExpense(recordId, request)
                } else {
                    expensesRepository.recordExpense(request)
                }

                result.fold(
                    onSuccess = { expense ->
                        if (receiptPart != null) {
                            expensesRepository.uploadReceipt(expense.id, receiptPart)
                        }
                        _recordState.value = AddRecordState.Success("expense")
                    },
                    onFailure = { error -> _recordState.value = AddRecordState.Error(error.message ?: "Failed to save expense") }
                )
            }
        }
    }

    fun resetState() {
        _recordState.value = AddRecordState.Idle
    }
}