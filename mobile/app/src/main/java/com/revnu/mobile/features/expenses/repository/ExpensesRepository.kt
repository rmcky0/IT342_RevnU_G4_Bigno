package com.revnu.mobile.features.expenses.repository

import com.revnu.mobile.core.network.RetrofitClient
import com.revnu.mobile.features.expenses.model.ExpenseRequest
import com.revnu.mobile.features.expenses.model.ExpenseResponse

class ExpensesRepository {

    suspend fun fetchExpenses(): Result<List<ExpenseResponse>> {
        return try {
            val response = RetrofitClient.apiService.getAllExpenses()
            val body = response.body()

            if (response.isSuccessful && body?.success == true && body.data != null) {
                // Spring Boot's Page object stores the list inside 'content'
                Result.success(body.data.content)
            } else {
                Result.failure(Exception(body?.message ?: "Failed to fetch expenses"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createExpense(request: ExpenseRequest): Result<ExpenseResponse> {
        return try {
            val response = RetrofitClient.apiService.recordExpense(request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Failed to record expense"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}