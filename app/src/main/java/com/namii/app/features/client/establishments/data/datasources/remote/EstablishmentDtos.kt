package com.namii.app.features.client.establishments.data.datasources.remote

import com.google.gson.annotations.SerializedName

data class BusinessHourDto(
    @SerializedName(value = "dayOfWeek", alternate = ["day_of_week"]) val dayOfWeek: Int,
    @SerializedName(value = "isClosed", alternate = ["is_closed"]) val isClosed: Boolean = false,
    @SerializedName(value = "opensAt", alternate = ["opens_at"]) val opensAt: String? = null,
    @SerializedName(value = "closesAt", alternate = ["closes_at"]) val closesAt: String? = null
)

data class MatchedProductDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName(value = "photoUrl", alternate = ["photo_url"]) val photoUrl: String? = null,
    @SerializedName("price") val price: Double? = null
)

data class ProductDto(
    @SerializedName("id") val id: Int,
    @SerializedName(value = "establishmentId", alternate = ["establishment_id"]) val establishmentId: Int = 0,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("price") val price: Double = 0.0,
    @SerializedName("category") val category: String = "",
    @SerializedName(value = "isAvailable", alternate = ["is_available"]) val isAvailable: Boolean = true,
    @SerializedName(value = "photoUrl", alternate = ["photo_url"]) val photoUrl: String = ""
)

data class EstablishmentDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("phone") val phone: String? = "",
    @SerializedName("address") val address: String? = "",
    @SerializedName("latitude") val latitude: Double = 0.0,
    @SerializedName("longitude") val longitude: Double = 0.0,
    @SerializedName(value = "coverPhotoUrl", alternate = ["cover_photo_url"]) val coverPhotoUrl: String? = null,
    @SerializedName(value = "isOpen", alternate = ["is_open"]) val isOpen: Boolean = false,
    @SerializedName(value = "averageRating", alternate = ["average_rating"]) val averageRating: Double = 0.0,
    @SerializedName(value = "ratingCount", alternate = ["rating_count"]) val ratingCount: Int = 0,
    @SerializedName(value = "isFavorite", alternate = ["is_favorite"]) val isFavorite: Boolean = false,
    @SerializedName(value = "userRating", alternate = ["user_rating"]) val userRating: Int? = null,
    @SerializedName(value = "businessHours", alternate = ["business_hours", "hours"]) val businessHours: List<BusinessHourDto>? = null,
    @SerializedName(value = "matchedProducts", alternate = ["matched_products"]) val matchedProducts: List<MatchedProductDto>? = null
)

data class EstablishmentDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("phone") val phone: String? = "",
    @SerializedName("address") val address: String? = "",
    @SerializedName("latitude") val latitude: Double = 0.0,
    @SerializedName("longitude") val longitude: Double = 0.0,
    @SerializedName(value = "coverPhotoUrl", alternate = ["cover_photo_url"]) val coverPhotoUrl: String? = null,
    @SerializedName(value = "isOpen", alternate = ["is_open"]) val isOpen: Boolean = false,
    @SerializedName(value = "averageRating", alternate = ["average_rating"]) val averageRating: Double = 0.0,
    @SerializedName(value = "ratingCount", alternate = ["rating_count"]) val ratingCount: Int = 0,
    @SerializedName(value = "isFavorite", alternate = ["is_favorite"]) val isFavorite: Boolean = false,
    @SerializedName(value = "userRating", alternate = ["user_rating"]) val userRating: Int? = null,
    @SerializedName(value = "businessHours", alternate = ["business_hours", "hours"]) val businessHours: List<BusinessHourDto>? = null,
    @SerializedName("products") val products: List<ProductDto>? = null
)
