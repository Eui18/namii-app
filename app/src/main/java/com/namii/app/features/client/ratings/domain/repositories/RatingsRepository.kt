package com.namii.app.features.client.ratings.domain.repositories

data class RatingSummary(
    val averageRating: Double,
    val ratingCount: Int,
    val userRating: Int
)

interface RatingsRepository {
    suspend fun rateEstablishment(establishmentId: Int, stars: Int): Result<RatingSummary>
}
