package com.revnu.mobile.features.auth.repository

import com.revnu.mobile.core.network.ApiService
import com.revnu.mobile.core.session.SessionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.revnu.mobile.core.network.ApiResponse
import com.revnu.mobile.features.auth.model.AuthResponse
import com.revnu.mobile.features.auth.model.GoogleAuthRequest
import com.revnu.mobile.features.auth.model.LoginRequest
import com.revnu.mobile.features.auth.model.RegisterRequest
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {

    private fun parseErrorMessage(errorBody: okhttp3.ResponseBody?, fallback: String): String {
        return try {
            val type = object : TypeToken<ApiResponse<AuthResponse>>() {}.type
            val parsed: ApiResponse<AuthResponse>? = Gson().fromJson(errorBody?.charStream(), type)
            parsed?.error?.message ?: fallback
        } catch (e: Exception) {
            fallback
        }
    }

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
                    val errorMsg = parseErrorMessage(response.errorBody(), response.message())
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
                    val errorMsg = parseErrorMessage(response.errorBody(), response.message())
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun loginWithGoogle(idToken: String): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("GoogleAuth", "Sending ID token to backend...")
                val response = apiService.googleAuth(GoogleAuthRequest(idToken))
                Log.d("GoogleAuth", "Backend response: code=${response.code()} success=${response.body()?.success}")

                if (response.isSuccessful && response.body()?.success == true) {
                    val authData = response.body()!!.data!!
                    Log.d("GoogleAuth", "Login success: role=${authData.role} hasRestaurant=${authData.hasRestaurant}")

                    sessionManager.saveAuthSession(
                        accessToken = authData.accessToken,
                        refreshToken = authData.refreshToken,
                        role = authData.role,
                        hasRestaurant = authData.hasRestaurant
                    )

                    Result.success(authData)
                } else {
                    val errorMsg = parseErrorMessage(response.errorBody(), response.message())
                    Log.e("GoogleAuth", "Backend rejected: $errorMsg (code=${response.code()})")
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Log.e("GoogleAuth", "Exception calling backend", e)
                Result.failure(e)
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }
}