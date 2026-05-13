package com.revnu.mobile.features.sales.repository

import com.revnu.mobile.core.network.RetrofitClient
import com.revnu.mobile.features.sales.model.SaleRequest
import com.revnu.mobile.features.sales.model.SaleResponse

class SalesRepository {

    suspend fun fetchSales(): Result<List<SaleResponse>> {
        return try {
            val response = RetrofitClient.apiService.getAllSales()
            val body = response.body()

            if (response.isSuccessful && body?.success == true && body.data != null) {
                // Spring Boot's Page object stores the list inside 'content'
                Result.success(body.data.content)
            } else {
                Result.failure(Exception(body?.message ?: "Failed to fetch sales"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSale(request: SaleRequest): Result<SaleResponse> {
        return try {
            val response = RetrofitClient.apiService.recordSale(request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Failed to record sale"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}