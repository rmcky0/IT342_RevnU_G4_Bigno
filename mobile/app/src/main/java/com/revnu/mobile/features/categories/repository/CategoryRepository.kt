package com.revnu.mobile.features.categories.repository

import com.revnu.mobile.core.network.ApiService
import com.revnu.mobile.features.categories.model.CategoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryRepository(private val apiService: ApiService) {

    suspend fun getCategories(type: String): Result<List<CategoryResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCategories(type)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.data ?: emptyList())
                } else {
                    Result.failure(Exception(response.body()?.error?.message ?: "Failed to load categories"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
