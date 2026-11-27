package com.namii.app.features.map.presentation.components

import androidx.annotation.ColorInt

/**
 * Modelo visual genérico de un marcador en el mapa compartido de Ñamii.
 */
data class MapPinItem(
    val id: Int,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    @param:ColorInt val categoryColorInt: Int
)

/**
 * Orden explícita para mover o centrar la cámara del mapa sin acoplarla
 * a cambios de coordenadas del marcador del vendedor.
 */
data class MapCameraCommand(
    val latitude: Double,
    val longitude: Double,
    val zoomLevel: Double,
    val animate: Boolean = true,
    val requestId: Long = 0L
)

/**
 * Límites geográficos visibles de la ventana actual del mapa.
 */
data class MapVisibleRegion(
    val north: Double,
    val south: Double,
    val east: Double,
    val west: Double
) {
    fun isValid(): Boolean =
        north > south && east > west && (north - south) > 1e-6 && (east - west) > 1e-6
}
