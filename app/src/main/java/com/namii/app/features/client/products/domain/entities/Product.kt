package com.namii.app.features.client.products.domain.entities

enum class ProductCategory(
    val apiKey: String,
    val displayName: String
) {
    BEBIDAS("Bebidas", "Bebidas"),
    ALIMENTOS("Alimentos", "Alimentos"),
    POSTRES("Postres", "Postres"),
    ANTOJITOS("Antojitos", "Antojitos");

    companion object {
        fun fromRaw(raw: String?): ProductCategory {
            if (raw.isNullOrBlank()) return ALIMENTOS
            return entries.firstOrNull {
                it.name.equals(raw.trim(), ignoreCase = true) ||
                    it.apiKey.equals(raw.trim(), ignoreCase = true)
            } ?: ALIMENTOS
        }
    }
}

data class Product(
    val id: Int,
    val establishmentId: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val category: ProductCategory,
    val isAvailable: Boolean,
    val photoUrl: String
)
