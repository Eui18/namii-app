package com.namii.app.features.seller.products.domain.usecases

import com.namii.app.features.client.products.domain.entities.Product
import com.namii.app.features.seller.products.domain.entities.SellerProductFormInput
import com.namii.app.features.seller.products.domain.entities.SellerProductValidators
import com.namii.app.features.seller.products.domain.repositories.SellerProductsRepository
import javax.inject.Inject

class GetMyProductsUseCase @Inject constructor(
    private val repository: SellerProductsRepository
) {
    suspend operator fun invoke(establishmentId: Int): Result<List<Product>> {
        return repository.getMyProducts(establishmentId)
    }
}

class GetSellerProductByIdUseCase @Inject constructor(
    private val repository: SellerProductsRepository
) {
    suspend operator fun invoke(productId: Int): Result<Product> {
        return repository.getProductById(productId)
    }
}

class SaveSellerProductUseCase @Inject constructor(
    private val repository: SellerProductsRepository
) {
    suspend operator fun invoke(
        establishmentId: Int,
        productId: Int?,
        input: SellerProductFormInput
    ): Result<Product> {
        SellerProductValidators.validateName(input.name)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        SellerProductValidators.validateDescription(input.description)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        if (input.price < 0.0) {
            return Result.failure(IllegalArgumentException("Ingresa un precio válido"))
        }
        SellerProductValidators.validatePhoto(
            localPhotoPath = input.localPhotoPath,
            existingPhotoUrl = input.existingPhotoUrl,
            isEditing = productId != null
        )?.let {
            return Result.failure(IllegalArgumentException(it))
        }

        return if (productId == null) {
            repository.createProduct(establishmentId, input)
        } else {
            repository.updateProduct(productId, input)
        }
    }
}

class ToggleSellerProductAvailabilityUseCase @Inject constructor(
    private val repository: SellerProductsRepository
) {
    suspend operator fun invoke(productId: Int, isAvailable: Boolean): Result<Product> {
        return repository.toggleProductAvailability(productId, isAvailable)
    }
}

class DeleteSellerProductUseCase @Inject constructor(
    private val repository: SellerProductsRepository
) {
    suspend operator fun invoke(productId: Int): Result<Unit> {
        return repository.deleteProduct(productId)
    }
}
