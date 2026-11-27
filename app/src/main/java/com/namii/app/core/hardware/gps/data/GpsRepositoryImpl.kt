package com.namii.app.core.hardware.gps.data

import com.namii.app.core.hardware.gps.domain.GpsRepository
import com.namii.app.core.hardware.gps.domain.model.LocationModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class GpsRepositoryImpl @Inject constructor(
    private val dataSource: GpsDataSource
) : GpsRepository {

    override fun hasGpsFeature(): Boolean = dataSource.hasGpsFeature()

    override fun hasLocationPermission(): Boolean = dataSource.hasLocationPermission()

    override fun getLocationStream(intervalMs: Long): Flow<LocationModel> {
        return dataSource.getLocationStream(intervalMs).map { it.toLocationModel() }
    }

    override suspend fun getSingleLocation(): Result<LocationModel> = runCatching {
        withTimeout(10_000L) {
            getLocationStream(1000L).first()
        }
    }
}
