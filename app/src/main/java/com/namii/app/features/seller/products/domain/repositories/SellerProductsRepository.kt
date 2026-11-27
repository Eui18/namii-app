package com.namii.app.features.seller.products.domain.repositories

import com.namii.app.features.client.products.domain.entities.Product
import com.namii.app.features.seller.products.domain.entities.SellerProductFormInput

interface SellerProductsRepository {
    suspend fun getMyProducts(establishmentId: Int): Result<List<Product>>
    suspend fun getProductById(productId: Int): Result<Product>
    suspend fun createProduct(establishmentId: Int, input: SellerProductFormInput): Result<Product>
    suspend fun updateProduct(productId: Int, input: SellerProductFormInput): Result<Product>
    suspend fun toggleProductAvailability(productId: Int, isAvailable: Boolean): Result<Product>
    suspend fun deleteProduct(productId: Int): Result<Unit>
}
