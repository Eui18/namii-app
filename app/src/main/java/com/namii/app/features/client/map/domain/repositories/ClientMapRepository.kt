package com.namii.app.features.client.map.domain.repositories

import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.domain.entities.MapBounds

interface ClientMapRepository {
    suspend fun getEstablishmentsInRegion(
        bounds: MapBounds?,
        query: String?
    ): Result<List<Establishment>>
}
