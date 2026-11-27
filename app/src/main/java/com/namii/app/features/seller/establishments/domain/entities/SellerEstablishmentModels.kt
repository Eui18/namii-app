package com.namii.app.features.seller.establishments.domain.entities

import com.namii.app.features.client.establishments.domain.entities.EstablishmentCategory

enum class ManualStatusOverride(val apiKey: String?) {
    NONE(null),
    OPEN("OPEN"),
    CLOSED("CLOSED");

    companion object {
        fun fromRaw(raw: String?): ManualStatusOverride {
            return when (raw?.trim()?.uppercase()) {
                "OPEN" -> OPEN
                "CLOSED" -> CLOSED
                else -> NONE
            }
        }
    }
}

data class DailySchedule(
    val dayOfWeek: Int,
    val isClosed: Boolean = false,
    val opensAt: String = "09:00",
    val closesAt: String = "18:00"
) {
    val dayLabel: String
        get() = when (dayOfWeek) {
            1 -> "Lunes"
            2 -> "Martes"
            3 -> "Miércoles"
            4 -> "Jueves"
            5 -> "Viernes"
            6 -> "Sábado"
            7 -> "Domingo"
            else -> "Día $dayOfWeek"
        }

    val shortDayLabel: String
        get() = when (dayOfWeek) {
            1 -> "Lun"
            2 -> "Mar"
            3 -> "Mié"
            4 -> "Jue"
            5 -> "Vie"
            6 -> "Sáb"
            7 -> "Dom"
            else -> ""
        }

    fun isTimeRangeValid(): Boolean {
        if (isClosed) return true
        val openMinutes = parseMinutes24h(opensAt) ?: return false
        val closeMinutes = parseMinutes24h(closesAt) ?: return false
        return openMinutes < closeMinutes
    }

    companion object {
        fun defaultWeek(): List<DailySchedule> = (1..7).map { day ->
            DailySchedule(
                dayOfWeek = day,
                isClosed = day == 7,
                opensAt = if (day == 6) "09:00" else "08:00",
                closesAt = if (day == 6) "16:00" else "20:00"
            )
        }

        fun parseMinutes24h(time: String?): Int? {
            if (time.isNullOrBlank()) return null
            val parts = time.trim().split(":")
            if (parts.size < 2) return null
            val hour = parts[0].toIntOrNull() ?: return null
            val minute = parts[1].toIntOrNull() ?: return null
            if (hour !in 0..23 || minute !in 0..59) return null
            return hour * 60 + minute
        }

        fun formatMinutes24h(hour: Int, minute: Int): String {
            return String.format("%02d:%02d", hour.coerceIn(0, 23), minute.coerceIn(0, 59))
        }
    }
}

data class SellerEstablishment(
    val id: Int,
    val ownerId: Int,
    val name: String,
    val category: EstablishmentCategory,
    val description: String?,
    val phone: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val coverPhotoUrl: String?,
    val isOpen: Boolean,
    val manualStatus: ManualStatusOverride,
    val isManuallyOverridden: Boolean,
    val businessHours: List<DailySchedule>,
    val averageRating: Double,
    val ratingCount: Int
)

data class CreateEstablishmentInput(
    val name: String,
    val category: EstablishmentCategory,
    val description: String?,
    val phone: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val localCoverPhotoPath: String?,
    val businessHours: List<DailySchedule>
)

data class UpdateEstablishmentInput(
    val name: String,
    val category: EstablishmentCategory,
    val description: String?,
    val phone: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val localCoverPhotoPath: String?,
    val removeCoverPhoto: Boolean,
    val businessHours: List<DailySchedule>
)

object SellerEstablishmentValidators {

    fun validateBusinessName(name: String): String? {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty() -> "Ingresa el nombre de tu negocio"
            trimmed.length > 120 -> "El nombre no puede superar 120 caracteres"
            else -> null
        }
    }

    fun validateBusinessPhone(phone: String): String? {
        val digits = phone.filter { it.isDigit() }
        return when {
            phone.trim().isEmpty() -> "Ingresa el teléfono de contacto"
            digits.length !in 7..15 -> "Ingresa un número válido de 7 a 15 dígitos"
            else -> null
        }
    }

    fun validateAddress(address: String): String? {
        val trimmed = address.trim()
        return when {
            trimmed.isEmpty() -> "Ingresa la dirección o referencia del negocio"
            trimmed.length > 255 -> "La dirección no puede superar 255 caracteres"
            else -> null
        }
    }

    fun validateDescription(description: String?): String? {
        val trimmed = description?.trim().orEmpty()
        return if (trimmed.length > 500) {
            "La descripción no puede superar 500 caracteres"
        } else {
            null
        }
    }

    fun validateCoordinates(lat: Double, lng: Double): String? {
        val invalid = lat == 0.0 && lng == 0.0 ||
            lat !in -90.0..90.0 || lng !in -180.0..180.0
        return if (invalid) {
            "Selecciona una ubicación válida en el mapa"
        } else {
            null
        }
    }

    fun validateSchedules(schedules: List<DailySchedule>): String? {
        if (schedules.size != 7) {
            return "Debes configurar los 7 días de la semana"
        }
        val invalidDay = schedules.firstOrNull { !it.isTimeRangeValid() }
        return if (invalidDay != null) {
            "En ${invalidDay.dayLabel}, la hora de apertura debe ser menor a la de cierre"
        } else {
            null
        }
    }
}
