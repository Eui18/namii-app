package com.namii.app.features.client.map.data.repositories

import com.namii.app.core.network.ApiErrorParser
import com.namii.app.features.client.establishments.data.datasources.remote.EstablishmentApiService
import com.namii.app.features.client.establishments.data.mapper.toDomain
import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.domain.entities.MapBounds
import com.namii.app.features.client.map.domain.repositories.ClientMapRepository
import javax.inject.Inject

class ClientMapRepositoryImpl @Inject constructor(
    private val apiService: EstablishmentApiService,
    private val errorParser: ApiErrorParser
) : ClientMapRepository {

    override suspend fun getEstablishmentsInRegion(
        bounds: MapBounds?,
        query: String?
    ): Result<List<Establishment>> {
        return try {
            val response = apiService.getEstablishmentsInMap(
                north = bounds?.north,
                south = bounds?.south,
                east = bounds?.east,
                west = bounds?.west,
                query = query
            )
            Result.success(response.map { it.toDomain() })
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }
}
