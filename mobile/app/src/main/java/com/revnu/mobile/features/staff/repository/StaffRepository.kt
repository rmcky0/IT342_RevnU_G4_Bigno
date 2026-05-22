package com.revnu.mobile.features.staff.repository

import com.revnu.mobile.core.network.ApiService
import com.revnu.mobile.features.staff.model.StaffRequest
import com.revnu.mobile.features.staff.model.StaffResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StaffRepository(
    private val apiService: ApiService
) {

    suspend fun getAllStaff(): Result<List<StaffResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllStaff()
                if (response.isSuccessful && response.body()?.success == true) {
                    val staffList = response.body()!!.data ?: emptyList()
                    Result.success(staffList)
                } else {
                    val errorMsg = response.body()?.error?.message ?: "Failed to load staff directory"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun createStaff(request: StaffRequest): Result<StaffResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createStaff(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.data!!)
                } else {
                    val errorMsg = response.body()?.error?.message ?: "Failed to add staff member"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateStaff(id: String, request: StaffRequest): Result<StaffResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateStaff(id, request)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.data!!)
                } else {
                    val errorMsg = response.body()?.error?.message ?: "Failed to update staff member"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteStaff(id: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteStaff(id)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(Unit)
                } else {
                    val errorMsg = response.body()?.error?.message ?: "Failed to remove staff member"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}