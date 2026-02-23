package com.example.hairup.api

import com.example.hairup.api.models.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/auth/register")
    fun register(@Body request: RegisterRequest): Call<LoginResponse>

    @GET("api/user/profile")
    fun getProfile(@Header("Authorization") token: String): Call<UserResponse>

    @PUT("api/user/profile")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Call<UserResponse>

    @POST("api/user/change-password")
    fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Call<Map<String, String>>

    @GET("api/appointments/next")
    fun getNextAppointment(@Header("Authorization") token: String): Call<AppointmentResponse>

    @GET("api/levels")
    fun getLevels(@Header("Authorization") token: String): Call<LevelsResponse>

    @GET("api/products")
    fun getProducts(@Header("Authorization") token: String): Call<ProductsResponse>

    @GET("api/appointments")
    fun getUserAppointments(@Header("Authorization") token: String): Call<AppointmentsResponse>

    @POST("api/appointments")
    fun createAppointment(
        @Header("Authorization") token: String,
        @Body request: CreateAppointmentRequest
    ): Call<AppointmentResponse>

    @PUT("api/appointments/{id}")
    fun updateAppointment(
        @Header("Authorization") token: String,
        @Path("id") appointmentId: Int,
        @Body request: UpdateAppointmentRequest
    ): Call<AppointmentResponse>

    @DELETE("api/appointments/{id}")
    fun deleteAppointment(
        @Header("Authorization") token: String,
        @Path("id") appointmentId: Int
    ): Call<Map<String, String>>

    @GET("api/services")
    fun getServices(@Header("Authorization") token: String): Call<ServicesResponse>

    @GET("api/admin-users")
    fun getBarbers(@Header("Authorization") token: String): Call<BarbersResponse>

    @GET("api/barbers/{barberId}/availability")
    fun getBarberAvailability(
        @Header("Authorization") token: String,
        @Path("barberId") barberId: Int,
        @Query("date") date: String
    ): Call<AvailabilityResponse>

    @GET("api/rewards")
    fun getRewards(@Header("Authorization") token: String): Call<RewardsResponse>

    @POST("api/rewards/redeem")
    fun redeemReward(
        @Header("Authorization") token: String,
        @Body request: RedeemRequest
    ): Call<RedeemResponse>

    @GET("api/categories")
    fun getCategories(@Header("Authorization") token: String): Call<CategoriesResponse>

    @POST("api/user/points/add")
    fun addPoints(
        @Header("Authorization") token: String,
        @Body request: AddPointsRequest
    ): Call<AddPointsResponse>

    @POST("api/admin/products")
    fun createProduct(
        @Header("Authorization") token: String,
        @Body request: CreateProductRequest
    ): Call<Map<String, Any>>

    @PUT("api/admin/products/{id}")
    fun updateProduct(
        @Header("Authorization") token: String,
        @Path("id") productId: Int,
        @Body request: UpdateProductRequest
    ): Call<Map<String, Any>>

    @DELETE("api/admin/products/{id}")
    fun deleteProduct(
        @Header("Authorization") token: String,
        @Path("id") productId: Int
    ): Call<Map<String, Any>>

    @POST("api/admin/services")
    fun createService(
        @Header("Authorization") token: String,
        @Body request: CreateServiceRequest
    ): Call<Map<String, Any>>

    @PUT("api/admin/services/{id}")
    fun updateService(
        @Header("Authorization") token: String,
        @Path("id") serviceId: Int,
        @Body request: UpdateServiceRequest
    ): Call<Map<String, Any>>

    @DELETE("api/admin/services/{id}")
    fun deleteService(
        @Header("Authorization") token: String,
        @Path("id") serviceId: Int
    ): Call<Map<String, Any>>

    @GET("api/admin/appointments")
    fun getAllAppointments(
        @Header("Authorization") token: String
    ): Call<AdminAppointmentsResponse>

    @GET("api/admin/dashboard")
    fun getDashboardStats(
        @Header("Authorization") token: String
    ): Call<DashboardStatsResponse>

    @GET("api/admin/users")
    fun getAllUsers(
        @Header("Authorization") token: String
    ): Call<AllUsersResponse>

    @POST("api/admin/make-admin")
    fun makeAdmin(
        @Header("Authorization") token: String,
        @Body request: ToggleAdminRequest
    ): Call<Map<String, Any>>

    @POST("api/admin/remove-admin")
    fun removeAdmin(
        @Header("Authorization") token: String,
        @Body request: ToggleAdminRequest
    ): Call<Map<String, Any>>

    @POST("api/admin/toggle-active")
    fun toggleActive(
        @Header("Authorization") token: String,
        @Body request: ToggleActiveRequest
    ): Call<Map<String, Any>>

    @POST("api/categories")
    fun createCategory(
        @Header("Authorization") token: String,
        @Body request: CreateCategoryRequest
    ): Call<CategorySuccessResponse>
}