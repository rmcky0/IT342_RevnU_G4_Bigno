package com.revnu.mobile.api

import com.revnu.mobile.model.AuthResponse
import com.revnu.mobile.model.LoginRequest
import com.revnu.mobile.model.RegisterRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("revnu/auth/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("revnu/auth/register")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>

    @GET("revnu/auth/me")
    fun getMe(): Call<ResponseBody>

//    @GET("sales")
//    fun getSales(): Call<ResponseBody>
//
//    @GET("expenses")
//    fun getExpenses(): Call<ResponseBody>
//
//    @GET("categories")
//    fun getCategories(): Call<ResponseBody>
//
//    @GET("dashboard/summary")
//    fun getSummary(): Call<ResponseBody>
}
