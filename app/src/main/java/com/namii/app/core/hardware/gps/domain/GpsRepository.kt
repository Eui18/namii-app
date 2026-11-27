package com.namii.app.core.hardware.gps.domain

import com.namii.app.core.hardware.gps.domain.model.LocationModel
import kotlinx.coroutines.flow.Flow

interface GpsRepository {
    fun hasGpsFeature(): Boolean
    fun hasLocationPermission(): Boolean
    fun getLocationStream(intervalMs: Long = 3000L): Flow<LocationModel>
    suspend fun getSingleLocation(): Result<LocationModel>
}
