package com.revnu.mobile.core.network

import android.content.Context
import android.content.Intent
import com.revnu.mobile.core.config.Constants
import com.revnu.mobile.core.session.SessionManager
import com.revnu.mobile.features.auth.ui.LoginActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private lateinit var sessionManager: SessionManager
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
        sessionManager = SessionManager(appContext)
    }

    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()

        if (::sessionManager.isInitialized) {
            val token = sessionManager.fetchAccessToken()
            if (!token.isNullOrBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
        }
        chain.proceed(requestBuilder.build())
    }

    private val refreshInterceptor = Interceptor { chain ->
        val request = chain.request()
        val response = chain.proceed(request)

        val path = request.url.encodedPath
        val isAuthEndpoint = path.contains("/auth/login") ||
                path.contains("/auth/register") ||
                path.contains("/auth/refresh")

        if (response.code == 401 && !isAuthEndpoint && ::sessionManager.isInitialized) {
            val refreshToken = sessionManager.fetchRefreshToken()

            if (!refreshToken.isNullOrBlank()) {
                synchronized(this) {
                    val currentToken = sessionManager.fetchAccessToken()
                    val previousToken = request.header("Authorization")?.removePrefix("Bearer ")

                    if (currentToken != null && currentToken != previousToken) {
                        // Token was already refreshed, just retry the original request
                        val newRequest = request.newBuilder()
                            .header("Authorization", "Bearer $currentToken")
                            .build()
                        response.close()
                        return@Interceptor chain.proceed(newRequest)
                    }

                    try {
                        val refreshResponse = apiService.refreshToken(
                            mapOf("refreshToken" to refreshToken)
                        ).execute()

                        if (refreshResponse.isSuccessful && refreshResponse.body()?.success == true) {
                            val newAuthData = refreshResponse.body()!!.data!!

                            sessionManager.saveAuthSession(
                                accessToken = newAuthData.accessToken,
                                refreshToken = newAuthData.refreshToken,
                                role = newAuthData.role
                            )

                            val newRequest = request.newBuilder()
                                .header("Authorization", "Bearer ${newAuthData.accessToken}")
                                .build()

                            response.close()
                            return@Interceptor chain.proceed(newRequest)
                        } else {
                            handleTotalAuthFailure()
                        }
                    } catch (e: Exception) {
                        handleTotalAuthFailure()
                    }
                }
            } else {
                handleTotalAuthFailure()
            }
        }

        response
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(refreshInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private fun handleTotalAuthFailure() {
        if (::sessionManager.isInitialized && ::appContext.isInitialized) {
            sessionManager.clearSession()
            val intent = Intent(appContext, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            appContext.startActivity(intent)
        }
    }
}