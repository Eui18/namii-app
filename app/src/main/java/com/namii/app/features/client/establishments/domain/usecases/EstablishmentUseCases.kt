package com.namii.app.features.client.establishments.domain.usecases

import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.domain.entities.EstablishmentCategory
import com.namii.app.features.client.establishments.domain.entities.EstablishmentDetail
import com.namii.app.features.client.establishments.domain.entities.MapBounds
import com.namii.app.features.client.establishments.domain.repositories.EstablishmentRepository
import javax.inject.Inject

class GetEstablishmentsByCategoryUseCase @Inject constructor(
    private val repository: EstablishmentRepository
) {
    suspend operator fun invoke(category: EstablishmentCategory?): Result<List<Establishment>> {
        return repository.getEstablishments(category).map { list ->
            list.sortedByDescending { it.averageRating }
        }
    }
}

class GetPopularEstablishmentsUseCase @Inject constructor(
    private val repository: EstablishmentRepository
) {
    suspend operator fun invoke(limit: Int = 10): Result<List<Establishment>> {
        return repository.getPopularEstablishments().map { list ->
            list.sortedByDescending { it.averageRating }.take(limit)
        }
    }
}

class SearchEstablishmentsUseCase @Inject constructor(
    private val repository: EstablishmentRepository
) {
    suspend operator fun invoke(query: String): Result<List<Establishment>> {
        val cleaned = query.trim()
        if (cleaned.isEmpty()) return Result.success(emptyList())
        return repository.searchEstablishments(cleaned)
    }
}

class GetEstablishmentsInMapUseCase @Inject constructor(
    private val repository: EstablishmentRepository
) {
    suspend operator fun invoke(bounds: MapBounds?, query: String? = null): Result<List<Establishment>> {
        val cleanedQuery = query?.trim()?.takeIf { it.isNotEmpty() }
        return repository.getEstablishmentsInMap(bounds, cleanedQuery)
    }
}

class GetEstablishmentDetailUseCase @Inject constructor(
    private val repository: EstablishmentRepository
) {
    suspend operator fun invoke(id: Int): Result<EstablishmentDetail> {
        return repository.getEstablishmentDetail(id)
    }
}
