package com.revnu.mobile.features.entry.model

sealed class AddRecordState {
    object Idle : AddRecordState()
    object Loading : AddRecordState()
    data class Success(val savedType: String) : AddRecordState()
    data class Error(val message: String) : AddRecordState()
}