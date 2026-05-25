package com.revnu.mobile.features.staff.repository

import com.revnu.mobile.core.network.ApiService
import com.revnu.mobile.features.staff.model.SalaryRequest
import com.revnu.mobile.features.staff.model.SalaryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SalaryRepository(
    private val apiService: ApiService
) {

    suspend fun getSalaryHistory(page: Int = 0, size: Int = 100): Result<List<SalaryResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getSalaryHistory(page = page, size = size)
                if (response.isSuccessful && response.body()?.success == true) {
                    val payrollList = response.body()!!.data?.content ?: emptyList()
                    Result.success(payrollList)
                } else {
                    val errorMsg = response.body()?.error?.message ?: "Failed to load payroll history"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun recordSalary(request: SalaryRequest): Result<SalaryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.recordSalary(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.data!!)
                } else {
                    val errorMsg = response.body()?.error?.message ?: "Failed to record payout"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateSalary(id: String, request: SalaryRequest): Result<SalaryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateSalary(id, request)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.data!!)
                } else {
                    val errorMsg = response.body()?.error?.message ?: "Failed to update payout record"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteSalary(id: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteSalary(id)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(Unit)
                } else {
                    val errorMsg = response.body()?.error?.message ?: "Failed to delete payout record"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getStaffSalaries(staffId: String): Result<List<SalaryResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getStaffSalaries(staffId)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.data ?: emptyList())
                } else {
                    val errorMsg = response.body()?.error?.message ?: "Failed to load individual staff payouts"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}