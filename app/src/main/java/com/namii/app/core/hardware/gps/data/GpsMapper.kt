package com.namii.app.core.hardware.gps.data

import android.location.Location
import com.namii.app.core.hardware.gps.domain.model.LocationModel

fun Location.toLocationModel(): LocationModel {
    return LocationModel(
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
        accuracy = accuracy,
        timestamp = time
    )
}
