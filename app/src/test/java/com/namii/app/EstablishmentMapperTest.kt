package com.namii.app

import com.namii.app.features.client.establishments.data.datasources.remote.BusinessHourDto
import com.namii.app.features.client.establishments.data.datasources.remote.EstablishmentDetailDto
import com.namii.app.features.client.establishments.data.datasources.remote.ProductDto
import com.namii.app.features.client.establishments.data.mapper.toDomain
import com.namii.app.features.client.establishments.domain.entities.EstablishmentCategory
import com.namii.app.features.client.products.domain.entities.ProductCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EstablishmentMapperTest {

    @Test
    fun detailDtoMapsCategoriesSoldOutProductsAnd24hSchedule() {
        val dto = EstablishmentDetailDto(
            id = 7,
            name = "Café Bonito",
            category = "Cafeterías",
            description = "Café de altura",
            phone = "9616024411",
            address = "Av. Central #452",
            latitude = 16.7528,
            longitude = -93.1158,
            coverPhotoUrl = "https://cdn.example.com/cafe.jpg",
            isOpen = true,
            averageRating = 4.8,
            ratingCount = 128,
            isFavorite = false,
            userRating = null,
            businessHours = listOf(
                BusinessHourDto(dayOfWeek = 1, isClosed = false, opensAt = "08:00:00", closesAt = "20:00:00"),
                BusinessHourDto(dayOfWeek = 6, isClosed = false, opensAt = "08:00:00", closesAt = "16:00:00"),
                BusinessHourDto(dayOfWeek = 7, isClosed = true, opensAt = null, closesAt = null)
            ),
            products = listOf(
                ProductDto(
                    id = 11,
                    establishmentId = 7,
                    name = "Tacos de Guisado",
                    description = "Chicharrón",
                    price = 65.0,
                    category = "Antojitos",
                    isAvailable = true,
                    photoUrl = "https://cdn.example.com/tacos.jpg"
                ),
                ProductDto(
                    id = 12,
                    establishmentId = 7,
                    name = "Pastel de Chocolate",
                    description = "Trufa casera",
                    price = 70.0,
                    category = "Postres",
                    isAvailable = false,
                    photoUrl = "https://cdn.example.com/pastel.jpg"
                )
            )
        )

        val detail = dto.toDomain()

        assertEquals(EstablishmentCategory.CAFETERIAS, detail.establishment.category)
        assertTrue(detail.establishment.scheduleSummary().contains("Sáb: 08:00 - 16:00"))
        assertTrue(detail.establishment.scheduleSummary().contains("Dom: Cerrado"))
        assertEquals(2, detail.products.size)
        assertEquals(ProductCategory.ANTOJITOS, detail.products[0].category)
        assertTrue(detail.products[0].isAvailable)
        assertEquals(ProductCategory.POSTRES, detail.products[1].category)
        assertFalse(detail.products[1].isAvailable)
    }
}
