package org.bosf.pooch.data.api

import org.bosf.pooch.data.api.models.auth.AuthResponse
import org.bosf.pooch.data.api.models.auth.LoginRequest
import org.bosf.pooch.data.api.models.auth.RegisterRequest
import org.bosf.pooch.data.api.models.auth.UserResponse
import org.bosf.pooch.data.api.models.dogs.CreateDogRequest
import org.bosf.pooch.data.api.models.dogs.DogResponse
import org.bosf.pooch.data.api.models.dogs.UpdateDogRequest
import org.bosf.pooch.data.api.models.products.ProductResponse
import org.bosf.pooch.data.api.models.scans.ScanHistoryResponse
import org.bosf.pooch.data.api.models.scans.ScanRequest
import org.bosf.pooch.data.api.models.scans.ScanResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(): Response<AuthResponse>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<UserResponse>

    @GET("dogs")
    suspend fun getDogs(): Response<List<DogResponse>>

    @POST("dogs")
    suspend fun createDog(@Body request: CreateDogRequest): Response<DogResponse>

    @GET("dogs/{id}")
    suspend fun getDog(@Path("id") id: String): Response<DogResponse>

    @PUT("dogs/{id}")
    suspend fun updateDog(
        @Path("id") id: String,
        @Body request: UpdateDogRequest
    ): Response<DogResponse>

    @DELETE("dogs/{id}")
    suspend fun deleteDog(@Path("id") id: String): Response<Unit>

    @POST("scans")
    suspend fun submitScan(@Body request: ScanRequest): Response<ScanResponse>

    @GET("scans/{dog_id}")
    suspend fun getScanHistory(
        @Path("dog_id") dogId: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<ScanHistoryResponse>

    @GET("scans/products/{barcode}")
    suspend fun getProductByBarcode(@Path("barcode") barcode: String): Response<ProductResponse>
}
