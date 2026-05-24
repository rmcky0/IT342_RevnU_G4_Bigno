package com.revnu.mobile.features.auth.repository

import com.revnu.mobile.core.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ForgotPasswordRepository(private val apiService: ApiService) {

    suspend fun requestOtp(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.forgotPassword(mapOf("email" to email))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                val msg = response.body()?.error?.message ?: response.message()
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyOtp(email: String, otp: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.verifyOtp(mapOf("email" to email, "otp" to otp))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                val msg = response.body()?.error?.message ?: response.message()
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String, otp: String, newPassword: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.resetPassword(
                    mapOf("email" to email, "otp" to otp, "newPassword" to newPassword)
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(Unit)
                } else {
                    val msg = response.body()?.error?.message ?: response.message()
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
