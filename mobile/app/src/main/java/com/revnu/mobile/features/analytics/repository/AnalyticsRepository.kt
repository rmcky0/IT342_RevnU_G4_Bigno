package com.revnu.mobile.features.analytics.repository

import android.util.Log
import com.revnu.mobile.core.network.ApiResponse
import com.revnu.mobile.core.network.ApiService
import com.revnu.mobile.features.analytics.model.DailyAnalyticsResponse
import retrofit2.Response
class AnalyticsRepository(private val apiService: ApiService) {

    suspend fun getDailyAnalytics(): Response<ApiResponse<DailyAnalyticsResponse>> {
        return apiService.getDailyStats()
    }
}