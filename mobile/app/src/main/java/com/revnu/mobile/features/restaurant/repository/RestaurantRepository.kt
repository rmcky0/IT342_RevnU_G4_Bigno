package com.revnu.mobile.features.restaurant.repository

import com.google.gson.Gson
import com.revnu.mobile.core.network.ApiService
import com.revnu.mobile.features.restaurant.model.RestaurantSetupRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class RestaurantRepository(
    private val apiService: ApiService
) {
    suspend fun setupRestaurant(request: RestaurantSetupRequest): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val json = Gson().toJson(request)
                val dataPart = json.toRequestBody("application/json".toMediaType())
                val response = apiService.setupRestaurant(data = dataPart, logo = null)

                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(Unit)
                } else {
                    val errorMsg = response.body()?.error?.message ?: response.message()
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}