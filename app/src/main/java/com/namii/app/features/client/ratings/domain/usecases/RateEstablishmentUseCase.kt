package com.namii.app.features.client.ratings.domain.usecases

import com.namii.app.features.client.ratings.domain.repositories.RatingSummary
import com.namii.app.features.client.ratings.domain.repositories.RatingsRepository
import javax.inject.Inject

class RateEstablishmentUseCase @Inject constructor(
    private val repository: RatingsRepository
) {
    suspend operator fun invoke(establishmentId: Int, stars: Int): Result<RatingSummary> {
        return repository.rateEstablishment(establishmentId, stars)
    }
}
