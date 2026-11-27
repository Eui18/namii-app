package com.namii.app.features.client.ratings.data.datasources.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

data class RateRequestDto(
    @SerializedName("stars") val stars: Int
)

data class RatingSummaryDto(
    @SerializedName(value = "averageRating", alternate = ["average_rating"]) val averageRating: Double,
    @SerializedName(value = "ratingCount", alternate = ["rating_count"]) val ratingCount: Int,
    @SerializedName(value = "userRating", alternate = ["user_rating", "stars"]) val userRating: Int
)

interface RatingsApiService {
    @PUT("establishments/{establishmentId}/rating")
    suspend fun rateEstablishment(
        @Path("establishmentId") establishmentId: Int,
        @Body body: RateRequestDto
    ): RatingSummaryDto
}
