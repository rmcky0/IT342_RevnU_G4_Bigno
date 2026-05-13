package com.revnu.mobile.core.network

import com.revnu.mobile.features.analytics.model.DailyAnalyticsResponse
import com.revnu.mobile.features.auth.model.AuthResponse
import com.revnu.mobile.features.auth.model.LoginRequest
import com.revnu.mobile.features.auth.model.RegisterRequest
import com.revnu.mobile.features.expenses.model.ExpenseRequest
import com.revnu.mobile.features.expenses.model.ExpenseResponse
import com.revnu.mobile.features.sales.model.CloseDayRequest
import com.revnu.mobile.features.sales.model.SaleRequest
import com.revnu.mobile.features.sales.model.SaleResponse
import com.revnu.mobile.features.staff.model.SalaryRequest
import com.revnu.mobile.features.staff.model.SalaryResponse
import com.revnu.mobile.features.staff.model.StaffRequest
import com.revnu.mobile.features.staff.model.StaffResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("revnu/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("revnu/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("revnu/auth/me")
    suspend fun getMe(): Response<AuthResponse>

    @GET("revnu/sales")
    suspend fun getAllSales(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<SaleResponse>>>

    @POST("revnu/sales")
    suspend fun recordSale(
        @Body request: SaleRequest
    ): Response<ApiResponse<SaleResponse>>

    @DELETE("revnu/sales/{id}")
    suspend fun deleteSale(
        @Path("id") id: String
    ): Response<ApiResponse<Any>>
    @PUT("revnu/sales/{id}")
    suspend fun updateSale(
        @Path("id") id: String,
        @Body request: SaleRequest
    ): Response<ApiResponse<SaleResponse>>


    // In ApiService.kt
    @GET("revnu/expenses")
    suspend fun getAllExpenses(): Response<ApiResponse<PageResponse<ExpenseResponse>>>

    @POST("revnu/expenses")
    suspend fun recordExpense(@Body request: ExpenseRequest): Response<ApiResponse<ExpenseResponse>>

    @PUT("revnu/expenses/{id}")
    suspend fun updateExpense(@Path("id") id: String, @Body request: ExpenseRequest): Response<ApiResponse<ExpenseResponse>>

    @Multipart
    @POST("revnu/expenses/{id}/upload")
    suspend fun uploadReceipt(
        @Path("id") id: String,
        @Part file: MultipartBody.Part
    ): Response<ApiResponse<ExpenseResponse>>
    @DELETE("revnu/expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: String): Response<ApiResponse<Any>>
    @POST("revnu/day/close")
    suspend fun closeDay(@Body request: CloseDayRequest): Response<Any>

    @GET("revnu/analytics/daily")
    suspend fun getDailyAnalytics(
        @Query("date") date: String? = null
    ): Response<ApiResponse<DailyAnalyticsResponse>>
    @GET("revnu/staff")
    suspend fun getStaff(): Response<ApiResponse<List<StaffResponse>>>

    @POST("revnu/staff")
    suspend fun addStaff(@Body request: StaffRequest): Response<ApiResponse<StaffResponse>>

    @PUT("revnu/staff/{id}") // Added: Missing in your version
    suspend fun updateStaff(@Path("id") id: String, @Body request: StaffRequest): Response<ApiResponse<StaffResponse>>

    @DELETE("revnu/staff/{id}")
    suspend fun deleteStaff(@Path("id") id: String): Response<ApiResponse<Any>>

    @GET("revnu/salaries") // Added: Missing in your version
    suspend fun getSalary(): Response<ApiResponse<List<SalaryResponse>>>

    @POST("revnu/salaries") // Added: Missing in your version
    suspend fun addSalary(@Body request: SalaryRequest): Response<ApiResponse<SalaryResponse>>
    @PUT("revnu/salaries/{id}")
    suspend fun updateSalary(
        @Path("id") id: String,
        @Body request: SalaryRequest
    ): Response<ApiResponse<SalaryResponse>>

    @DELETE("revnu/salaries/{id}")
    suspend fun deleteSalary(
        @Path("id") id: String
    ): Response<ApiResponse<Any>>
    // Standard Delete (reusable if your backend handles it by ID)

}
