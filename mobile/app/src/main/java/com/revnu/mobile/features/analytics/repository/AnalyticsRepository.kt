package com.revnu.mobile.features.analytics.repository

import com.revnu.mobile.core.network.ApiResponse
import com.revnu.mobile.core.network.ApiService
import com.revnu.mobile.features.analytics.model.DailyAnalyticsResponse
import com.revnu.mobile.features.expenses.model.ExpenseResponse
import com.revnu.mobile.features.sales.model.CloseDayRequest
import com.revnu.mobile.features.sales.model.SaleResponse
import com.revnu.mobile.features.staff.model.SalaryResponse
import retrofit2.Response

class AnalyticsRepository(private val apiService: ApiService) {

    suspend fun getDailyAnalytics(): Response<ApiResponse<DailyAnalyticsResponse>> {
        return apiService.getDailyStats()
    }

    suspend fun getOpenSales(): List<SaleResponse> {
        return try {
            val res = apiService.getAllSales(page = 0, size = 9999)
            res.body()?.data?.content?.filter { it.status != "CLOSED" } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getOpenExpenses(): List<ExpenseResponse> {
        return try {
            val res = apiService.getAllExpenses(page = 0, size = 9999)
            res.body()?.data?.content?.filter { it.status != "CLOSED" } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllSalaries(): List<SalaryResponse> {
        return try {
            val res = apiService.getSalaryHistory(page = 0, size = 9999)
            res.body()?.data?.content ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun closeDay(date: String): Result<Unit> {
        return try {
            val response = apiService.closeDay(CloseDayRequest(date))
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
