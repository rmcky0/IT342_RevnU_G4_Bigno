package com.revnu.mobile.features.expenses.repository

import com.revnu.mobile.core.network.ApiService
import com.revnu.mobile.features.expenses.model.ExpenseRequest
import com.revnu.mobile.features.expenses.model.ExpenseResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

class ExpensesRepository(
    private val apiService: ApiService
) {
    suspend fun getTodayExpenses(): Result<List<ExpenseResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllExpenses() // Ensure you have default pagination (page=0, size=100) in ApiService
                if (response.isSuccessful && response.body()?.success == true) {
                    val expensesList = response.body()!!.data?.content ?: emptyList()
                    Result.success(expensesList)
                } else {
                    Result.failure(Exception(response.body()?.error?.message ?: "Failed to load expenses"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteExpense(id: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteExpense(id)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.body()?.error?.message ?: "Failed to delete expense"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun uploadReceipt(id: String, filePart: MultipartBody.Part): Result<ExpenseResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.uploadReceipt(id, filePart)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.data!!)
                } else {
                    Result.failure(Exception(response.body()?.error?.message ?: "Failed to upload receipt"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun closeDay(date: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.closeDay(
                    com.revnu.mobile.features.sales.model.CloseDayRequest(date)
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.body()?.error?.message ?: "Failed to lock day"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // You still need these because AddRecordViewModel calls them!
    suspend fun recordExpense(request: ExpenseRequest): Result<ExpenseResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.recordExpense(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.data!!)
                } else {
                    Result.failure(Exception(response.body()?.error?.message ?: "Failed to save"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateExpense(id: String, request: ExpenseRequest): Result<ExpenseResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateExpense(id, request)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.data!!)
                } else {
                    Result.failure(Exception(response.body()?.error?.message ?: "Failed to update"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}