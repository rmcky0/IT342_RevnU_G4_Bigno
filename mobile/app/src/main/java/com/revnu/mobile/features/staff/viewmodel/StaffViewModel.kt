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

    private var lastFetchedAt = 0L

    fun loadStaff(force: Boolean = false) {
        val now = System.currentTimeMillis()
        if (!force && lastFetchedAt > 0 && (now - lastFetchedAt) < CACHE_TTL_MS && _staffList.value.isNotEmpty()) return

        viewModelScope.launch {
            repository.getAllStaff().onSuccess {
                _staffList.value = it
                lastFetchedAt = System.currentTimeMillis()
            }
        }
    }

    fun forceRefresh() = loadStaff(force = true)

    companion object {
        private const val CACHE_TTL_MS = 5 * 60 * 1000L
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