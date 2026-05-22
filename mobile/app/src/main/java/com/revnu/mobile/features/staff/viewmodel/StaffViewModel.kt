package com.revnu.mobile.features.staff.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revnu.mobile.features.staff.model.StaffRequest
import com.revnu.mobile.features.staff.model.StaffResponse
import com.revnu.mobile.features.staff.repository.StaffRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StaffViewModel(private val repository: StaffRepository) : ViewModel() {
    private val _staffList = MutableStateFlow<List<StaffResponse>>(emptyList())
    val staffList: StateFlow<List<StaffResponse>> = _staffList.asStateFlow()

    fun loadStaff() {
        viewModelScope.launch {
            repository.getAllStaff().onSuccess { _staffList.value = it }
        }
    }

    fun addStaff(request: StaffRequest) {
        viewModelScope.launch {
            repository.createStaff(request).onSuccess { loadStaff() }
        }
    }

    fun updateStaff(id: String, request: StaffRequest) {
        viewModelScope.launch {
            repository.updateStaff(id, request).onSuccess { loadStaff() }
        }
    }

    fun deleteStaff(id: String) {
        viewModelScope.launch {
            repository.deleteStaff(id).onSuccess { loadStaff() }
        }
    }
}