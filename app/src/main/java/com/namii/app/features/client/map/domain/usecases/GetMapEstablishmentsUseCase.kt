package com.namii.app.features.client.map.domain.usecases

import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.domain.entities.MapBounds
import com.namii.app.features.client.map.domain.repositories.ClientMapRepository
import javax.inject.Inject

class GetMapEstablishmentsUseCase @Inject constructor(
    private val repository: ClientMapRepository
) {
    suspend operator fun invoke(
        bounds: MapBounds?,
        query: String? = null
    ): Result<List<Establishment>> {
        val cleanedQuery = query?.trim()?.takeIf { it.isNotEmpty() }
        return repository.getEstablishmentsInRegion(bounds, cleanedQuery)
    }
}
