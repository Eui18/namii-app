package com.namii.app.features.client.establishments.data.mapper

import com.namii.app.features.client.establishments.data.datasources.remote.BusinessHourDto
import com.namii.app.features.client.establishments.data.datasources.remote.EstablishmentDetailDto
import com.namii.app.features.client.establishments.data.datasources.remote.EstablishmentDto
import com.namii.app.features.client.establishments.data.datasources.remote.MatchedProductDto
import com.namii.app.features.client.establishments.data.datasources.remote.ProductDto
import com.namii.app.features.client.establishments.domain.entities.BusinessHour
import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.domain.entities.EstablishmentCategory
import com.namii.app.features.client.establishments.domain.entities.EstablishmentDetail
import com.namii.app.features.client.establishments.domain.entities.MatchedProduct
import com.namii.app.features.client.products.domain.entities.Product
import com.namii.app.features.client.products.domain.entities.ProductCategory

fun BusinessHourDto.toDomain(): BusinessHour {
    return BusinessHour(
        dayOfWeek = dayOfWeek,
        isClosed = isClosed,
        opensAt = opensAt,
        closesAt = closesAt
    )
}

fun MatchedProductDto.toDomain(): MatchedProduct {
    return MatchedProduct(
        id = id,
        name = name,
        photoUrl = photoUrl,
        price = price
    )
}

fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        establishmentId = establishmentId,
        name = name,
        description = description,
        price = price,
        category = ProductCategory.fromRaw(category),
        isAvailable = isAvailable,
        photoUrl = photoUrl
    )
}

fun EstablishmentDto.toDomain(): Establishment {
    return Establishment(
        id = id,
        name = name,
        category = EstablishmentCategory.fromRaw(category),
        description = description,
        phone = phone.orEmpty(),
        address = address.orEmpty(),
        latitude = latitude,
        longitude = longitude,
        coverPhotoUrl = coverPhotoUrl,
        isOpen = isOpen,
        averageRating = averageRating,
        ratingCount = ratingCount,
        isFavorite = isFavorite,
        userRating = userRating,
        businessHours = businessHours?.map { it.toDomain() }.orEmpty(),
        matchedProducts = matchedProducts?.map { it.toDomain() }.orEmpty()
    )
}

fun EstablishmentDetailDto.toDomain(): EstablishmentDetail {
    val est = Establishment(
        id = id,
        name = name,
        category = EstablishmentCategory.fromRaw(category),
        description = description,
        phone = phone.orEmpty(),
        address = address.orEmpty(),
        latitude = latitude,
        longitude = longitude,
        coverPhotoUrl = coverPhotoUrl,
        isOpen = isOpen,
        averageRating = averageRating,
        ratingCount = ratingCount,
        isFavorite = isFavorite,
        userRating = userRating,
        businessHours = businessHours?.map { it.toDomain() }.orEmpty(),
        matchedProducts = emptyList()
    )
    return EstablishmentDetail(
        establishment = est,
        products = products?.map { it.toDomain() }.orEmpty()
    )
}
