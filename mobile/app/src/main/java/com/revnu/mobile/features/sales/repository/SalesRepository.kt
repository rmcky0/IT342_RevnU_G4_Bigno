package com.revnu.mobile.features.sales.repository

import com.revnu.mobile.core.network.ApiService
import com.revnu.mobile.features.sales.model.CloseDayRequest // Assuming you have this model
import com.revnu.mobile.features.sales.model.SaleRequest
import com.revnu.mobile.features.sales.model.SaleResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SalesRepository(
    private val apiService: ApiService
) {

    suspend fun getTodaySales(): Result<List<SaleResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                // Fetching the first page of sales.
                val response = apiService.getAllSales(page = 0, size = 100)

                if (response.isSuccessful && response.body()?.success == true) {
                    val salesList = response.body()!!.data?.content ?: emptyList()
                    Result.success(salesList)
                } else {
                    val errorMsg = response.body()?.error?.message ?: "Failed to load sales"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteSale(id: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteSale(id)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(Unit)
                } else {
                    val errorMsg = response.body()?.error?.message ?: "Failed to delete sale"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun closeDay(date: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val request = CloseDayRequest(date = date)
                val response = apiService.closeDay(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(Unit)
                } else {
                    val errorMsg = response.body()?.error?.message ?: "Failed to lock EOD"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun recordSale(request: SaleRequest): Result<SaleResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.recordSale(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.data!!)
                } else {
                    Result.failure(Exception(response.body()?.error?.message ?: "Failed to save sale"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // You still need this here because AddRecordActivity will call it!
    suspend fun updateSale(id: String, request: SaleRequest): Result<SaleResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateSale(id, request)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.data!!)
                } else {
                    Result.failure(Exception(response.body()?.error?.message ?: "Failed to update sale"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}