package com.namii.app.features.auth.data.datasources.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String
)

data class UserResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String
)

data class AuthResponseDto(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserResponseDto
)

interface AuthApiService {
    @POST("auth/client/login")
    suspend fun loginClient(@Body request: LoginRequestDto): AuthResponseDto

    @POST("auth/client/register")
    suspend fun registerClient(@Body request: RegisterRequestDto): AuthResponseDto

    @POST("auth/seller/login")
    suspend fun loginSeller(@Body request: LoginRequestDto): AuthResponseDto

    @POST("auth/seller/register")
    suspend fun registerSeller(@Body request: RegisterRequestDto): AuthResponseDto
}
