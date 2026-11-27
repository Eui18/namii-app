package com.namii.app.features.seller.establishments.data.datasources.remote

import com.google.gson.annotations.SerializedName
import com.namii.app.features.client.establishments.data.datasources.remote.BusinessHourDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

data class SellerBusinessHourRequestDto(
    @SerializedName("dayOfWeek") val dayOfWeek: Int,
    @SerializedName("isClosed") val isClosed: Boolean,
    @SerializedName("opensAt") val opensAt: String?,
    @SerializedName("closesAt") val closesAt: String?
)

data class CreateSellerEstablishmentRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("description") val description: String?,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("businessHours") val businessHours: List<SellerBusinessHourRequestDto>
)

data class UpdateSellerEstablishmentRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("description") val description: String?,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("removeCoverPhoto") val removeCoverPhoto: Boolean,
    @SerializedName("businessHours") val businessHours: List<SellerBusinessHourRequestDto>
)

data class UpdateManualStatusRequestDto(
    @SerializedName("manualStatus") val manualStatus: String?
)

data class SellerEstablishmentDto(
    @SerializedName("id") val id: Int,
    @SerializedName(value = "ownerId", alternate = ["owner_id"]) val ownerId: Int = 0,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("phone") val phone: String? = "",
    @SerializedName("address") val address: String? = "",
    @SerializedName("latitude") val latitude: Double = 0.0,
    @SerializedName("longitude") val longitude: Double = 0.0,
    @SerializedName(value = "coverPhotoUrl", alternate = ["cover_photo_url"]) val coverPhotoUrl: String? = null,
    @SerializedName(value = "isOpen", alternate = ["is_open"]) val isOpen: Boolean = false,
    @SerializedName(value = "manualStatus", alternate = ["manual_status"]) val manualStatus: String? = null,
    @SerializedName(value = "isManuallyOverridden", alternate = ["is_manually_overridden"]) val isManuallyOverridden: Boolean? = null,
    @SerializedName(value = "businessHours", alternate = ["business_hours", "hours"]) val businessHours: List<BusinessHourDto>? = null,
    @SerializedName(value = "averageRating", alternate = ["average_rating"]) val averageRating: Double = 0.0,
    @SerializedName(value = "ratingCount", alternate = ["rating_count"]) val ratingCount: Int = 0
)

interface SellerEstablishmentApiService {
    @GET("seller/establishment")
    suspend fun getMyEstablishment(): SellerEstablishmentDto

    @POST("seller/establishment")
    suspend fun createEstablishment(
        @Body body: CreateSellerEstablishmentRequestDto
    ): SellerEstablishmentDto

    @PUT("seller/establishment/{id}")
    suspend fun updateEstablishment(
        @Path("id") id: Int,
        @Body body: UpdateSellerEstablishmentRequestDto
    ): SellerEstablishmentDto

    @PATCH("seller/establishment/{id}/status")
    suspend fun updateManualStatus(
        @Path("id") id: Int,
        @Body body: UpdateManualStatusRequestDto
    ): SellerEstablishmentDto

    @Multipart
    @POST("seller/establishment/{id}/cover")
    suspend fun uploadCoverPhoto(
        @Path("id") id: Int,
        @Part coverPhoto: MultipartBody.Part
    ): SellerEstablishmentDto

    @DELETE("seller/establishment/{id}/cover")
    suspend fun deleteCoverPhoto(
        @Path("id") id: Int
    ): SellerEstablishmentDto
}
