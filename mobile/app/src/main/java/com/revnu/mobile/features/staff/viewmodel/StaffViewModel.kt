package com.revnu.mobile.features.staff.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revnu.mobile.core.network.RetrofitClient
import com.revnu.mobile.features.staff.model.SalaryRequest
import com.revnu.mobile.features.staff.model.SalaryResponse
import com.revnu.mobile.features.staff.model.StaffRequest
import com.revnu.mobile.features.staff.model.StaffResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.UUID

class StaffViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    // ── State ─────────────────────────────────────────────────────────────────

    private val _staffList = MutableStateFlow<List<StaffResponse>>(emptyList())
    val staffList: StateFlow<List<StaffResponse>> = _staffList

    private val _payrollList = MutableStateFlow<List<SalaryResponse>>(emptyList())
    val payrollList: StateFlow<List<SalaryResponse>> = _payrollList

    private val _isLocked = MutableStateFlow(false)
    val isLocked: StateFlow<Boolean> = _isLocked

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // ── Load ──────────────────────────────────────────────────────────────────

    fun loadData(isDirectory: Boolean) {
        if (isDirectory) loadStaff() else loadPayroll()
    }

    private fun loadStaff() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.getStaff()
                if (response.isSuccessful) {
                    _staffList.value = response.body()?.data ?: emptyList()
                } else {
                    _error.value = "Failed to load staff: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadPayroll() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.getSalary()
                if (response.isSuccessful) {
                    _payrollList.value = response.body()?.data ?: emptyList()
                } else {
                    _error.value = "Failed to load payroll: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Staff CRUD ────────────────────────────────────────────────────────────

    fun addStaff(fullname: String, position: String, salaryRate: BigDecimal) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.addStaff(StaffRequest(fullname, position, salaryRate))
                if (response.isSuccessful) {
                    _successMessage.value = "$fullname added to the team."
                    loadStaff()
                } else {
                    _error.value = "Failed to add staff: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateStaff(id: String, fullname: String, position: String, salaryRate: BigDecimal) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.updateStaff(id, StaffRequest(fullname, position, salaryRate))
                if (response.isSuccessful) {
                    _successMessage.value = "Staff record updated."
                    loadStaff()
                } else {
                    _error.value = "Failed to update staff: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteStaff(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.deleteStaff(id)
                if (response.isSuccessful) {
                    _successMessage.value = "Staff member removed."
                    loadStaff()
                } else {
                    _error.value = "Failed to delete staff: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Salary CRUD ───────────────────────────────────────────────────────────

    fun addSalary(staffId: String, amount: BigDecimal, paymentDate: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request  = SalaryRequest(UUID.fromString(staffId), amount, java.time.LocalDate.parse(paymentDate))
                val response = api.addSalary(request)
                if (response.isSuccessful) {
                    _successMessage.value = "Salary recorded successfully."
                    loadPayroll()
                } else {
                    _error.value = "Failed to record salary: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSalary(id: String, staffId: String, amount: BigDecimal, paymentDate: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request  = SalaryRequest(UUID.fromString(staffId), amount, java.time.LocalDate.parse(paymentDate))
                // Use the payroll endpoint — adjust path if your API differs
                val response = api.updateSalary(id, request)
                if (response.isSuccessful) {
                    _successMessage.value = "Salary record updated."
                    loadPayroll()
                } else {
                    _error.value = "Failed to update salary: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteSalary(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.deleteSalary(id)
                if (response.isSuccessful) {
                    _successMessage.value = "Salary record deleted."
                    loadPayroll()
                } else {
                    _error.value = "Failed to delete salary: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    fun clearError()   { _error.value = null }
    fun clearSuccess() { _successMessage.value = null }
}