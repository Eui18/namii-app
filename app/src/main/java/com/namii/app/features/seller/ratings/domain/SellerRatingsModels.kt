package com.namii.app.features.seller.ratings.domain

import com.namii.app.features.seller.establishments.domain.entities.SellerEstablishment
import javax.inject.Inject

data class SellerRatingSummary(
    val averageRating: Double,
    val ratingCount: Int
)

class BuildSellerRatingSummaryUseCase @Inject constructor() {
    operator fun invoke(establishment: SellerEstablishment): SellerRatingSummary {
        return SellerRatingSummary(
            averageRating = establishment.averageRating,
            ratingCount = establishment.ratingCount
        )
    }
}
