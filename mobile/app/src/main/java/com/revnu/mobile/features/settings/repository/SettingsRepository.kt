package com.revnu.mobile.features.settings.repository

import com.revnu.mobile.core.network.ApiService
import com.revnu.mobile.features.settings.model.ChangePasswordRequest
import com.revnu.mobile.features.settings.model.RestaurantProfileRequest
import com.revnu.mobile.features.settings.model.RestaurantProfileResponse
import com.revnu.mobile.features.settings.model.UpdateProfileRequest
import com.revnu.mobile.features.settings.model.UserProfileResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

class SettingsRepository(private val apiService: ApiService) {

    suspend fun getProfile(): Result<UserProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getProfile()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "Failed to load profile"))
            }
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateProfile(request: UpdateProfileRequest): Result<UserProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateProfile(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "Failed to update profile"))
            }
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun changePassword(request: ChangePasswordRequest): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.changePassword(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "Failed to change password"))
            }
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getRestaurantProfile(): Result<RestaurantProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getRestaurantProfile()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "Failed to load restaurant profile"))
            }
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateRestaurantProfile(request: RestaurantProfileRequest): Result<RestaurantProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateRestaurantProfile(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "Failed to update restaurant profile"))
            }
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateRestaurantLogo(filePart: MultipartBody.Part): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateRestaurantLogo(filePart)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "Failed to upload logo"))
            }
        } catch (e: Exception) { Result.failure(e) }
    }
}
