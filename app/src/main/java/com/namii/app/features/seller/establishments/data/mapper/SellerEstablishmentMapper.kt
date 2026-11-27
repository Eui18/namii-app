package com.namii.app.features.seller.establishments.data.mapper

import com.namii.app.features.client.establishments.data.datasources.remote.BusinessHourDto
import com.namii.app.features.client.establishments.domain.entities.EstablishmentCategory
import com.namii.app.features.seller.establishments.data.datasources.remote.SellerBusinessHourRequestDto
import com.namii.app.features.seller.establishments.data.datasources.remote.SellerEstablishmentDto
import com.namii.app.features.seller.establishments.domain.entities.DailySchedule
import com.namii.app.features.seller.establishments.domain.entities.ManualStatusOverride
import com.namii.app.features.seller.establishments.domain.entities.SellerEstablishment

fun BusinessHourDto.toSellerDomain(): DailySchedule {
    return DailySchedule(
        dayOfWeek = dayOfWeek,
        isClosed = isClosed,
        opensAt = normalizeHhMm(opensAt) ?: "09:00",
        closesAt = normalizeHhMm(closesAt) ?: "18:00"
    )
}

fun DailySchedule.toRequestDto(): SellerBusinessHourRequestDto {
    return SellerBusinessHourRequestDto(
        dayOfWeek = dayOfWeek,
        isClosed = isClosed,
        opensAt = if (isClosed) null else opensAt,
        closesAt = if (isClosed) null else closesAt
    )
}

fun SellerEstablishmentDto.toDomain(): SellerEstablishment {
    val parsedManual = ManualStatusOverride.fromRaw(manualStatus)
    val sortedHours = normalizeFullWeek(businessHours?.map { it.toSellerDomain() }.orEmpty())
    return SellerEstablishment(
        id = id,
        ownerId = ownerId,
        name = name,
        category = EstablishmentCategory.fromRaw(category),
        description = description,
        phone = phone.orEmpty(),
        address = address.orEmpty(),
        latitude = latitude,
        longitude = longitude,
        coverPhotoUrl = coverPhotoUrl,
        isOpen = isOpen,
        manualStatus = parsedManual,
        isManuallyOverridden = isManuallyOverridden ?: (parsedManual != ManualStatusOverride.NONE),
        businessHours = sortedHours,
        averageRating = averageRating,
        ratingCount = ratingCount
    )
}

private fun normalizeHhMm(raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    val parts = raw.trim().split(":")
    if (parts.size < 2) return null
    val h = parts[0].toIntOrNull()?.coerceIn(0, 23) ?: return null
    val m = parts[1].toIntOrNull()?.coerceIn(0, 59) ?: return null
    return String.format("%02d:%02d", h, m)
}

private fun normalizeFullWeek(existing: List<DailySchedule>): List<DailySchedule> {
    val byDay = existing.associateBy { it.dayOfWeek }
    return (1..7).map { day ->
        byDay[day] ?: DailySchedule(
            dayOfWeek = day,
            isClosed = true,
            opensAt = "09:00",
            closesAt = "18:00"
        )
    }
}
