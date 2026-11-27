package com.namii.app.features.seller.products.domain.entities

import com.namii.app.features.client.products.domain.entities.ProductCategory

data class SellerProductFormInput(
    val name: String,
    val category: ProductCategory,
    val description: String?,
    val price: Double,
    val isAvailable: Boolean,
    val localPhotoPath: String?,
    val existingPhotoUrl: String? = null
)

object SellerProductValidators {

    fun validateName(name: String): String? {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty() -> "Ingresa el nombre del producto"
            trimmed.length > 120 -> "El nombre no puede superar 120 caracteres"
            else -> null
        }
    }

    fun validatePrice(priceInput: String): String? {
        val trimmed = priceInput.trim().replace(",", ".")
        val parsed = trimmed.toDoubleOrNull()
        return when {
            trimmed.isEmpty() -> "Ingresa el precio"
            parsed == null || parsed < 0.0 -> "Ingresa un precio válido mayor o igual a 0"
            parsed > 999999.99 -> "El precio es demasiado alto"
            else -> null
        }
    }

    fun validateDescription(description: String?): String? {
        val trimmed = description?.trim().orEmpty()
        return if (trimmed.length > 400) {
            "La descripción no puede superar 400 caracteres"
        } else {
            null
        }
    }

    fun validatePhoto(localPhotoPath: String?, existingPhotoUrl: String?, isEditing: Boolean): String? {
        val hasLocal = !localPhotoPath.isNullOrBlank()
        val hasExisting = isEditing && !existingPhotoUrl.isNullOrBlank()
        return if (!hasLocal && !hasExisting) {
            "Agrega una foto para el producto"
        } else {
            null
        }
    }
}
