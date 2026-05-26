package com.revnu.mobile.features.analytics.repository

import android.util.Log
import com.revnu.mobile.core.network.ApiResponse
import com.revnu.mobile.core.network.ApiService
import com.revnu.mobile.features.analytics.model.DailyAnalyticsResponse
import com.revnu.mobile.features.sales.model.CloseDayRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class AnalyticsRepository(private val apiService: ApiService) {

    suspend fun getDailyAnalytics(): Response<ApiResponse<DailyAnalyticsResponse>> {
        return apiService.getDailyStats()
    }

    suspend fun getOpenPayrollTotal(): Double {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getSalaryHistory(page = 0, size = 9999)
                if (response.isSuccessful && response.body()?.success == true) {
                    val records = response.body()!!.data?.content ?: emptyList()
                    records.filter { it.status != "CLOSED" }.sumOf { it.amount }
                } else 0.0
            } catch (e: Exception) {
                0.0
            }
        }
    }

    suspend fun closeDay(date: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
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
}