package com.revnu.mobile.core.network

import com.revnu.mobile.features.analytics.model.DailyAnalyticsResponse
import com.revnu.mobile.features.auth.model.AuthResponse
import com.revnu.mobile.features.categories.model.CategoryResponse
import com.revnu.mobile.features.auth.model.GoogleAuthRequest
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
import com.revnu.mobile.features.settings.model.ChangePasswordRequest
import com.revnu.mobile.features.settings.model.RestaurantProfileRequest
import com.revnu.mobile.features.settings.model.RestaurantProfileResponse
import com.revnu.mobile.features.settings.model.UpdateProfileRequest
import com.revnu.mobile.features.settings.model.UserProfileResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ==========================================
    // 1. AUTHENTICATION
    // ==========================================
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/refresh") // Required for Interceptor, must use Call
    fun refreshToken(@Body request: Map<String, String>): Call<ApiResponse<AuthResponse>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Any>>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: Map<String, String>): Response<ApiResponse<Any>>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: Map<String, String>): Response<ApiResponse<Any>>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: Map<String, String>): Response<ApiResponse<Any>>

    @POST("auth/google")
    suspend fun googleAuth(@Body request: GoogleAuthRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/link-google")
    suspend fun linkGoogle(@Body request: Map<String, String>): Response<ApiResponse<Any>>

    @GET("auth/me")
    suspend fun getMe(): Response<ApiResponse<AuthResponse>>


    // ==========================================
    // 2. SALES
    // ==========================================
    @POST("sales")
    suspend fun recordSale(@Body request: SaleRequest): Response<ApiResponse<SaleResponse>>

    @GET("sales")
    suspend fun getAllSales(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("date") date: String? = null
    ): Response<ApiResponse<PageResponse<SaleResponse>>>

    @GET("sales/date")
    suspend fun getSalesByDate(@Query("date") date: String): Response<ApiResponse<List<SaleResponse>>>

    @PUT("sales/{id}")
    suspend fun updateSale(@Path("id") id: String, @Body request: SaleRequest): Response<ApiResponse<SaleResponse>>

    @DELETE("sales/{id}")
    suspend fun deleteSale(@Path("id") id: String): Response<ApiResponse<Any>>


    // ==========================================
    // 3. EXPENSES
    // ==========================================
    @POST("expenses")
    suspend fun recordExpense(@Body request: ExpenseRequest): Response<ApiResponse<ExpenseResponse>>

    @GET("expenses")
    suspend fun getAllExpenses(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Response<ApiResponse<PageResponse<ExpenseResponse>>>

    @PUT("expenses/{id}")
    suspend fun updateExpense(@Path("id") id: String, @Body request: ExpenseRequest): Response<ApiResponse<ExpenseResponse>>

    @DELETE("expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: String): Response<ApiResponse<Any>>

    @Multipart
    @POST("expenses/{id}/upload")
    suspend fun uploadReceipt(@Path("id") id: String, @Part file: MultipartBody.Part): Response<ApiResponse<ExpenseResponse>>


    // ==========================================
    // 4. CATEGORIES
    // ==========================================
    @GET("categories")
    suspend fun getCategories(@Query("type") type: String? = null): Response<ApiResponse<List<CategoryResponse>>>

    @POST("categories")
    suspend fun createCategory(@Body request: Any): Response<ApiResponse<Any>>

    @PUT("categories/{id}")
    suspend fun updateCategory(@Path("id") id: String, @Body request: Any): Response<ApiResponse<Any>>

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String): Response<ApiResponse<Any>>


    // ==========================================
    // 5. STAFF
    // ==========================================
    @GET("staff")
    suspend fun getAllStaff(): Response<ApiResponse<List<StaffResponse>>>

    @GET("staff/{id}")
    suspend fun getStaffById(@Path("id") id: String): Response<ApiResponse<StaffResponse>>

    @POST("staff")
    suspend fun createStaff(@Body request: StaffRequest): Response<ApiResponse<StaffResponse>>

    @PUT("staff/{id}")
    suspend fun updateStaff(@Path("id") id: String, @Body request: StaffRequest): Response<ApiResponse<StaffResponse>>

    @DELETE("staff/{id}")
    suspend fun deleteStaff(@Path("id") id: String): Response<ApiResponse<Any>>


    // ==========================================
    // 6. SALARIES
    // ==========================================
    @POST("salaries")
    suspend fun recordSalary(@Body request: SalaryRequest): Response<ApiResponse<SalaryResponse>>

    @GET("salaries")
    suspend fun getSalaryHistory(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Response<ApiResponse<PageResponse<SalaryResponse>>>

    @GET("salaries/staff/{staffId}")
    suspend fun getStaffSalaries(@Path("staffId") staffId: String): Response<ApiResponse<List<SalaryResponse>>>

    @PUT("salaries/{id}")
    suspend fun updateSalary(@Path("id") id: String, @Body request: SalaryRequest): Response<ApiResponse<SalaryResponse>>

    @DELETE("salaries/{id}")
    suspend fun deleteSalary(@Path("id") id: String): Response<ApiResponse<Any>>


    // ==========================================
    // 7. REPORTING (EOD)
    // ==========================================
    @POST("day/close")
    suspend fun closeDay(@Body request: CloseDayRequest): Response<ApiResponse<Any>>

    @GET("day/summary/{date}")
    suspend fun getSummary(@Path("date") date: String): Response<ApiResponse<Any>>

    @GET("day/summaries")
    suspend fun getAllSummaries(): Response<ApiResponse<List<Any>>>

    @GET("day/detail/{date}")
    suspend fun getDayDetail(@Path("date") date: String): Response<ApiResponse<Any>>


    // ==========================================
    // 8. ANALYTICS
    // ==========================================
    @GET("analytics/daily")
    suspend fun getDailyStats(@Query("date") date: String? = null): Response<ApiResponse<DailyAnalyticsResponse>>

    @GET("analytics/trends")
    suspend fun getProfitTrends(@Query("period") period: String): Response<ApiResponse<Any>>


    // ==========================================
    // 9. SETTINGS (User, System, Restaurant)
    // ==========================================
    @GET("settings/profile")
    suspend fun getProfile(): Response<ApiResponse<UserProfileResponse>>

    @PUT("settings/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<UserProfileResponse>>

    @Multipart
    @PATCH("settings/profile/avatar")
    suspend fun updateProfilePicture(@Part file: MultipartBody.Part): Response<ApiResponse<Any>>

    @PUT("settings/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse<Any>>

    @GET("settings/restaurant")
    suspend fun getRestaurantProfile(): Response<ApiResponse<RestaurantProfileResponse>>

    @PUT("settings/restaurant")
    suspend fun updateRestaurantProfile(@Body request: RestaurantProfileRequest): Response<ApiResponse<RestaurantProfileResponse>>

    @Multipart
    @PATCH("settings/restaurant/logo")
    suspend fun updateRestaurantLogo(@Part file: MultipartBody.Part): Response<ApiResponse<Any>>

    @Multipart
    @POST("settings/restaurant")
    suspend fun setupRestaurant(
        @Part("data") data: okhttp3.RequestBody,
        @Part logo: MultipartBody.Part? = null
    ): Response<ApiResponse<Any>>

    @GET("settings/system")
    suspend fun getSystemSettings(): Response<ApiResponse<Any>>

    @PUT("settings/system")
    suspend fun updateSystemSettings(@Body request: Any): Response<ApiResponse<Any>>


    // ==========================================
    // 10. ADMIN
    // ==========================================
    @GET("admin/stats")
    suspend fun getStats(): Response<ApiResponse<Any>>

    @GET("admin/users")
    suspend fun getAllUsers(): Response<ApiResponse<List<Any>>>

    @PUT("admin/users/{id}/status")
    suspend fun updateUserStatus(@Path("id") id: String, @Body status: Any): Response<ApiResponse<Any>>

    @PUT("admin/users/{id}/role")
    suspend fun updateUserRole(@Path("id") id: String, @Body role: Any): Response<ApiResponse<Any>>

    @DELETE("admin/users/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<ApiResponse<Any>>

    @GET("admin/restaurants")
    suspend fun getAllRestaurants(): Response<ApiResponse<List<Any>>>


    // ==========================================
    // 11. NOTIFICATIONS
    // ==========================================
    @GET("notifications")
    suspend fun getNotifications(@Query("limit") limit: Int? = null): Response<ApiResponse<List<Any>>>

    @GET("notifications/unread-count")
    suspend fun getUnreadCount(): Response<ApiResponse<Any>>

    @PATCH("notifications/{id}/read")
    suspend fun markRead(@Path("id") id: String): Response<ApiResponse<Any>>

    @PATCH("notifications/read-all")
    suspend fun markAllRead(): Response<ApiResponse<Any>>

    @POST("notifications/holiday")
    suspend fun createHolidayAlert(@Body request: Any): Response<ApiResponse<Any>>

    @POST("notifications/system")
    suspend fun createSystemAlert(@Body request: Any): Response<ApiResponse<Any>>


    // ==========================================
    // 12. EXTERNAL
    // ==========================================
    @GET("external/holidays")
    suspend fun getHolidays(@Query("year") year: Int): Response<ApiResponse<List<Any>>>
}