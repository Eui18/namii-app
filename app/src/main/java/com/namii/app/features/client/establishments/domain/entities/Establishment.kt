package com.namii.app.features.client.establishments.domain.entities

import com.namii.app.features.client.products.domain.entities.Product

data class BusinessHour(
    val dayOfWeek: Int, // 1 = Lunes .. 7 = Domingo
    val isClosed: Boolean,
    val opensAt: String?, // Formato 24h HH:mm
    val closesAt: String? // Formato 24h HH:mm
) {
    fun shortDayName(): String = when (dayOfWeek) {
        1 -> "Lun"
        2 -> "Mar"
        3 -> "Mié"
        4 -> "Jue"
        5 -> "Vie"
        6 -> "Sáb"
        7 -> "Dom"
        else -> ""
    }

    fun formattedRange(): String {
        return if (isClosed || opensAt.isNullOrBlank() || closesAt.isNullOrBlank()) {
            "${shortDayName()}: Cerrado"
        } else {
            val open = opensAt.take(5)
            val close = closesAt.take(5)
            "${shortDayName()}: $open - $close"
        }
    }
}

data class MatchedProduct(
    val id: Int,
    val name: String,
    val photoUrl: String?,
    val price: Double?
)

data class Establishment(
    val id: Int,
    val name: String,
    val category: EstablishmentCategory,
    val description: String?,
    val phone: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val coverPhotoUrl: String?,
    val isOpen: Boolean,
    val averageRating: Double,
    val ratingCount: Int,
    val isFavorite: Boolean,
    val userRating: Int?,
    val businessHours: List<BusinessHour> = emptyList(),
    val matchedProducts: List<MatchedProduct> = emptyList()
) {
    fun scheduleSummary(): String {
        if (businessHours.isEmpty()) return "Horario no especificado"
        val openHours = businessHours.filter { !it.isClosed && !it.opensAt.isNullOrBlank() && !it.closesAt.isNullOrBlank() }
        if (openHours.isEmpty()) return "Cerrado"
        return businessHours
            .sortedBy { it.dayOfWeek }
            .joinToString(" · ") { it.formattedRange() }
    }
}

data class EstablishmentDetail(
    val establishment: Establishment,
    val products: List<Product>
)

data class MapBounds(
    val north: Double,
    val south: Double,
    val east: Double,
    val west: Double
)
