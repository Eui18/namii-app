package com.namii.app.features.client.establishments.data.repositories

import com.namii.app.core.network.ApiErrorParser
import com.namii.app.features.client.establishments.data.datasources.remote.EstablishmentApiService
import com.namii.app.features.client.establishments.data.mapper.toDomain
import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.domain.entities.EstablishmentCategory
import com.namii.app.features.client.establishments.domain.entities.EstablishmentDetail
import com.namii.app.features.client.establishments.domain.entities.MapBounds
import com.namii.app.features.client.establishments.domain.repositories.EstablishmentRepository
import javax.inject.Inject

class EstablishmentRepositoryImpl @Inject constructor(
    private val apiService: EstablishmentApiService,
    private val errorParser: ApiErrorParser
) : EstablishmentRepository {

    override suspend fun getEstablishments(
        category: EstablishmentCategory?
    ): Result<List<Establishment>> {
        return try {
            val response = apiService.getEstablishments(
                category = category?.apiKey
            )
            Result.success(response.map { it.toDomain() })
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }

    override suspend fun getPopularEstablishments(): Result<List<Establishment>> {
        return try {
            val response = apiService.getEstablishments()
            Result.success(response.map { it.toDomain() })
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }

    override suspend fun searchEstablishments(
        query: String
    ): Result<List<Establishment>> {
        return try {
            val response = apiService.getEstablishments(
                search = query.trim().takeIf { it.isNotEmpty() }
            )
            Result.success(response.map { it.toDomain() })
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }

    override suspend fun getEstablishmentsInMap(
        bounds: MapBounds?,
        query: String?
    ): Result<List<Establishment>> {
        return try {
            val response = apiService.getEstablishments(
                search = query?.trim()?.takeIf { it.isNotEmpty() },
                minLat = bounds?.south,
                maxLat = bounds?.north,
                minLng = bounds?.west,
                maxLng = bounds?.east
            )
            Result.success(response.map { it.toDomain() })
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }

    override suspend fun getEstablishmentDetail(
        id: Int
    ): Result<EstablishmentDetail> {
        return try {
            val response = apiService.getEstablishmentDetail(id)
            Result.success(response.toDomain())
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }
}
