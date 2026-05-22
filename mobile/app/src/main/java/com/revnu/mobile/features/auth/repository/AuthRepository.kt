package com.revnu.mobile.features.auth.repository

import com.revnu.mobile.core.network.ApiService
import com.revnu.mobile.core.session.SessionManager
import com.revnu.mobile.features.auth.model.AuthResponse
import com.revnu.mobile.features.auth.model.LoginRequest
import com.revnu.mobile.features.auth.model.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {

    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    val authData = response.body()!!.data!!

                    sessionManager.saveAuthSession(
                        accessToken = authData.accessToken,
                        refreshToken = authData.refreshToken,
                        role = authData.role,
                        hasRestaurant = authData.hasRestaurant
                    )

                    Result.success(authData)
                } else {
                    val errorMsg = response.body()?.error?.message ?: response.message()
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    val authData = response.body()!!.data!!

                    sessionManager.saveAuthSession(
                        accessToken = authData.accessToken,
                        refreshToken = authData.refreshToken,
                        role = authData.role,
                        hasRestaurant = authData.hasRestaurant
                    )

                    Result.success(authData)
                } else {
                    val errorMsg = response.body()?.error?.message ?: response.message()
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }
}