package com.namii.app.features.client.ratings.data.repositories

import com.namii.app.core.network.ApiErrorParser
import com.namii.app.features.client.ratings.data.datasources.remote.RateRequestDto
import com.namii.app.features.client.ratings.data.datasources.remote.RatingsApiService
import com.namii.app.features.client.ratings.domain.repositories.RatingSummary
import com.namii.app.features.client.ratings.domain.repositories.RatingsRepository
import javax.inject.Inject

class RatingsRepositoryImpl @Inject constructor(
    private val apiService: RatingsApiService,
    private val errorParser: ApiErrorParser
) : RatingsRepository {

    override suspend fun rateEstablishment(
        establishmentId: Int,
        stars: Int
    ): Result<RatingSummary> {
        return try {
            val coerced = stars.coerceIn(1, 5)
            val response = apiService.rateEstablishment(
                establishmentId = establishmentId,
                body = RateRequestDto(stars = coerced)
            )
            Result.success(
                RatingSummary(
                    averageRating = response.averageRating,
                    ratingCount = response.ratingCount,
                    userRating = response.userRating
                )
            )
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }
}
