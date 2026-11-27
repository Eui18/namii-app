package com.namii.app

import com.namii.app.features.seller.establishments.domain.entities.DailySchedule
import com.namii.app.features.seller.establishments.domain.entities.SellerEstablishmentValidators
import com.namii.app.features.seller.products.domain.entities.SellerProductValidators
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SellerValidatorsTest {

    @Test
    fun perDayScheduleAllowsDifferentSaturdayHours() {
        val schedules = (1..7).map { day ->
            when (day) {
                in 1..5 -> DailySchedule(dayOfWeek = day, isClosed = false, opensAt = "08:00", closesAt = "20:00")
                6 -> DailySchedule(dayOfWeek = 6, isClosed = false, opensAt = "08:00", closesAt = "16:00")
                else -> DailySchedule(dayOfWeek = 7, isClosed = true, opensAt = "09:00", closesAt = "18:00")
            }
        }

        assertNull(SellerEstablishmentValidators.validateSchedules(schedules))
    }

    @Test
    fun scheduleFailsWhenCloseTimeIsBeforeOrEqualOpenTime() {
        val invalidSchedules = (1..7).map { day ->
            if (day == 6) {
                DailySchedule(dayOfWeek = 6, isClosed = false, opensAt = "16:00", closesAt = "08:00")
            } else {
                DailySchedule(dayOfWeek = day, isClosed = false, opensAt = "08:00", closesAt = "20:00")
            }
        }

        val error = SellerEstablishmentValidators.validateSchedules(invalidSchedules)
        assertNotNull(error)
        assertTrue(error!!.contains("Sábado"))
    }

    @Test
    fun establishmentRequiredAndOptionalFieldsValidatedCorrectly() {
        assertNull(SellerEstablishmentValidators.validateBusinessName("Cafetería La Ceiba"))
        assertNotNull(SellerEstablishmentValidators.validateBusinessName("   "))

        assertNull(SellerEstablishmentValidators.validateBusinessPhone("9611234567"))
        assertNotNull(SellerEstablishmentValidators.validateBusinessPhone("123"))

        assertNull(SellerEstablishmentValidators.validateAddress("Av. Central Norte #140"))
        assertNotNull(SellerEstablishmentValidators.validateAddress(""))

        assertNull(SellerEstablishmentValidators.validateDescription(null))
        assertNull(SellerEstablishmentValidators.validateCoordinates(16.7528, -93.1158))
        assertNotNull(SellerEstablishmentValidators.validateCoordinates(0.0, 0.0))
    }

    @Test
    fun productValidatorsAllowZeroOrPositivePriceAndEditExistingPhoto() {
        assertNull(SellerProductValidators.validateName("Tacos al pastor"))
        assertNotNull(SellerProductValidators.validateName(""))

        assertNull(SellerProductValidators.validatePrice("0"))
        assertNull(SellerProductValidators.validatePrice("85.50"))
        assertNotNull(SellerProductValidators.validatePrice("-10"))
        assertNotNull(SellerProductValidators.validatePrice("abc"))

        assertNotNull(SellerProductValidators.validatePhoto(null, null, isEditing = false))
        assertNull(SellerProductValidators.validatePhoto(null, "https://img/tacos.jpg", isEditing = true))
        assertNull(SellerProductValidators.validatePhoto("/cache/photo.jpg", null, isEditing = false))
    }
}
