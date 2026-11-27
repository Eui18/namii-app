package com.namii.app.features.client.establishments.domain.repositories

import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.domain.entities.EstablishmentCategory
import com.namii.app.features.client.establishments.domain.entities.EstablishmentDetail
import com.namii.app.features.client.establishments.domain.entities.MapBounds

interface EstablishmentRepository {
    suspend fun getEstablishments(category: EstablishmentCategory? = null): Result<List<Establishment>>
    suspend fun getPopularEstablishments(): Result<List<Establishment>>
    suspend fun searchEstablishments(query: String): Result<List<Establishment>>
    suspend fun getEstablishmentsInMap(bounds: MapBounds?, query: String? = null): Result<List<Establishment>>
    suspend fun getEstablishmentDetail(id: Int): Result<EstablishmentDetail>
}
