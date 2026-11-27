package com.namii.app.features.seller.products.data.repositories

import com.namii.app.core.network.ApiErrorParser
import com.namii.app.features.client.establishments.data.mapper.toDomain
import com.namii.app.features.client.products.domain.entities.Product
import com.namii.app.features.seller.products.data.datasources.remote.SellerProductAvailabilityRequestDto
import com.namii.app.features.seller.products.data.datasources.remote.SellerProductsApiService
import com.namii.app.features.seller.products.domain.entities.SellerProductFormInput
import com.namii.app.features.seller.products.domain.repositories.SellerProductsRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.Locale
import javax.inject.Inject

class SellerProductsRepositoryImpl @Inject constructor(
    private val apiService: SellerProductsApiService,
    private val errorParser: ApiErrorParser
) : SellerProductsRepository {

    override suspend fun getMyProducts(establishmentId: Int): Result<List<Product>> {
        return try {
            val dtos = apiService.getMyProducts(establishmentId)
            Result.success(dtos.map { it.toDomain() })
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }

    override suspend fun getProductById(productId: Int): Result<Product> {
        return try {
            Result.success(apiService.getProductById(productId).toDomain())
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }

    override suspend fun createProduct(
        establishmentId: Int,
        input: SellerProductFormInput
    ): Result<Product> {
        return try {
            val path = input.localPhotoPath ?: return Result.failure(
                IllegalArgumentException("Agrega una foto para el producto")
            )
            val photoFile = File(path)
            if (!photoFile.exists()) {
                return Result.failure(IllegalArgumentException("La imagen seleccionada no existe"))
            }

            val photoPart = MultipartBody.Part.createFormData(
                "photo",
                photoFile.name,
                photoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            val textType = "text/plain".toMediaTypeOrNull()

            val created = apiService.createProduct(
                establishmentId = establishmentId,
                name = input.name.trim().toRequestBody(textType),
                category = input.category.apiKey.toRequestBody(textType),
                description = input.description?.trim()?.takeIf { it.isNotEmpty() }?.toRequestBody(textType),
                price = String.format(Locale.US, "%.2f", input.price).toRequestBody(textType),
                isAvailable = input.isAvailable.toString().toRequestBody(textType),
                photo = photoPart
            )
            Result.success(created.toDomain())
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }

    override suspend fun updateProduct(
        productId: Int,
        input: SellerProductFormInput
    ): Result<Product> {
        return try {
            val photoPart = input.localPhotoPath
                ?.takeIf { it.isNotBlank() }
                ?.let { File(it) }
                ?.takeIf { it.exists() }
                ?.let { file ->
                    MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    )
                }

            val textType = "text/plain".toMediaTypeOrNull()
            val updated = apiService.updateProduct(
                productId = productId,
                name = input.name.trim().toRequestBody(textType),
                category = input.category.apiKey.toRequestBody(textType),
                description = input.description?.trim()?.takeIf { it.isNotEmpty() }?.toRequestBody(textType),
                price = String.format(Locale.US, "%.2f", input.price).toRequestBody(textType),
                isAvailable = input.isAvailable.toString().toRequestBody(textType),
                photo = photoPart
            )
            Result.success(updated.toDomain())
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }

    override suspend fun toggleProductAvailability(
        productId: Int,
        isAvailable: Boolean
    ): Result<Product> {
        return try {
            val updated = apiService.toggleAvailability(
                productId = productId,
                body = SellerProductAvailabilityRequestDto(isAvailable = isAvailable)
            )
            Result.success(updated.toDomain())
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }

    override suspend fun deleteProduct(productId: Int): Result<Unit> {
        return try {
            apiService.deleteProduct(productId)
            Result.success(Unit)
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }
}
