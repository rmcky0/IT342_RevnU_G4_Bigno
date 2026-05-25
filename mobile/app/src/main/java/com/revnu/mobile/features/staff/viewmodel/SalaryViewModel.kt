package com.revnu.mobile.features.staff.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revnu.mobile.features.staff.model.SalaryRequest
import com.revnu.mobile.features.staff.model.SalaryResponse
import com.revnu.mobile.features.staff.repository.SalaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SalaryViewModel(private val repository: SalaryRepository) : ViewModel() {

    private var allPayroll = listOf<SalaryResponse>()

    private val _filteredPayroll = MutableStateFlow<List<SalaryResponse>>(emptyList())
    val filteredPayroll: StateFlow<List<SalaryResponse>> = _filteredPayroll.asStateFlow()

    private val _currentFilterMonth = MutableStateFlow("")
    val currentFilterMonth: StateFlow<String> = _currentFilterMonth.asStateFlow()

    init {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        _currentFilterMonth.value = sdf.format(Date())
    }

    fun loadPayroll() {
        viewModelScope.launch {
            repository.getSalaryHistory().onSuccess { salaries ->
                allPayroll = salaries
                applyMonthFilter(_currentFilterMonth.value)
            }
        }
    }

    fun setMonthFilter(yearMonth: String) {
        _currentFilterMonth.value = yearMonth
        applyMonthFilter(yearMonth)
    }

    private fun applyMonthFilter(yearMonth: String) {
        _filteredPayroll.value = allPayroll.filter { it.paymentDate.startsWith(yearMonth) }
    }

    fun addSalary(request: SalaryRequest) {
        viewModelScope.launch {
            repository.recordSalary(request).onSuccess { loadPayroll() }
        }
    }

    fun updateSalary(id: String, request: SalaryRequest) {
        viewModelScope.launch {
            repository.updateSalary(id, request).onSuccess { loadPayroll() }
        }
    }

    fun deleteSalary(id: String) {
        viewModelScope.launch {
            repository.deleteSalary(id).onSuccess { loadPayroll() }
        }
    }
}