package com.namii.app

import com.namii.app.features.client.establishments.data.datasources.remote.BusinessHourDto
import com.namii.app.features.client.establishments.domain.entities.EstablishmentCategory
import com.namii.app.features.seller.establishments.data.datasources.remote.SellerEstablishmentDto
import com.namii.app.features.seller.establishments.data.mapper.toDomain
import com.namii.app.features.seller.establishments.domain.entities.ManualStatusOverride
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SellerEstablishmentMapperTest {

    @Test
    fun mapsSellerEstablishmentAndNormalizesFullWeek24h() {
        val dto = SellerEstablishmentDto(
            id = 42,
            ownerId = 7,
            name = "Panadería El Sol",
            category = "Panaderías",
            description = null,
            phone = "9615551234",
            address = "Calle 5a Poniente #22",
            latitude = 16.7511,
            longitude = -93.1205,
            coverPhotoUrl = null,
            isOpen = true,
            manualStatus = "OPEN",
            isManuallyOverridden = true,
            businessHours = listOf(
                BusinessHourDto(dayOfWeek = 1, isClosed = false, opensAt = "07:00:00", closesAt = "21:00:00"),
                BusinessHourDto(dayOfWeek = 6, isClosed = false, opensAt = "08:00:00", closesAt = "14:00:00")
            ),
            averageRating = 4.8,
            ratingCount = 29
        )

        val mapped = dto.toDomain()

        assertEquals(42, mapped.id)
        assertEquals(EstablishmentCategory.PANADERIAS, mapped.category)
        assertNull(mapped.coverPhotoUrl)
        assertTrue(mapped.isOpen)
        assertTrue(mapped.isManuallyOverridden)
        assertEquals(ManualStatusOverride.OPEN, mapped.manualStatus)
        assertEquals(7, mapped.businessHours.size)
        assertEquals("07:00", mapped.businessHours.first { it.dayOfWeek == 1 }.opensAt)
        assertEquals("14:00", mapped.businessHours.first { it.dayOfWeek == 6 }.closesAt)
        assertTrue(mapped.businessHours.first { it.dayOfWeek == 7 }.isClosed)
        assertFalse(mapped.businessHours.first { it.dayOfWeek == 6 }.isClosed)
    }
}
