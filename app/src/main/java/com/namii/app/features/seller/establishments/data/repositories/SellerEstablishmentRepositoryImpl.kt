package com.namii.app.features.seller.establishments.data.repositories

import com.namii.app.core.network.ApiErrorParser
import com.namii.app.features.seller.establishments.data.datasources.remote.CreateSellerEstablishmentRequestDto
import com.namii.app.features.seller.establishments.data.datasources.remote.SellerEstablishmentApiService
import com.namii.app.features.seller.establishments.data.datasources.remote.UpdateManualStatusRequestDto
import com.namii.app.features.seller.establishments.data.datasources.remote.UpdateSellerEstablishmentRequestDto
import com.namii.app.features.seller.establishments.data.mapper.toDomain
import com.namii.app.features.seller.establishments.data.mapper.toRequestDto
import com.namii.app.features.seller.establishments.domain.entities.CreateEstablishmentInput
import com.namii.app.features.seller.establishments.domain.entities.ManualStatusOverride
import com.namii.app.features.seller.establishments.domain.entities.SellerEstablishment
import com.namii.app.features.seller.establishments.domain.entities.UpdateEstablishmentInput
import com.namii.app.features.seller.establishments.domain.repositories.SellerEstablishmentRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

class SellerEstablishmentRepositoryImpl @Inject constructor(
    private val apiService: SellerEstablishmentApiService,
    private val errorParser: ApiErrorParser
) : SellerEstablishmentRepository {

    override suspend fun getMyEstablishment(): Result<SellerEstablishment?> {
        return try {
            val dto = apiService.getMyEstablishment()
            Result.success(dto.toDomain())
        } catch (http: HttpException) {
            if (http.code() == 404) {
                val rawBody = runCatching { http.response()?.errorBody()?.string() }.getOrNull().orEmpty()
                if (!rawBody.contains("Ruta no encontrada", ignoreCase = true)) {
                    return Result.success(null)
                }
            }
            Result.failure(Exception(errorParser.parse(http)))
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }

    override suspend fun createEstablishment(input: CreateEstablishmentInput): Result<SellerEstablishment> {
        return try {
            var dto = apiService.createEstablishment(
                CreateSellerEstablishmentRequestDto(
                    name = input.name.trim(),
                    category = input.category.apiKey,
                    description = input.description?.trim()?.takeIf { it.isNotEmpty() },
                    phone = input.phone.trim(),
                    address = input.address.trim(),
                    latitude = input.latitude,
                    longitude = input.longitude,
                    businessHours = input.businessHours.map { it.toRequestDto() }
                )
            )

            val photoPath = input.localCoverPhotoPath
            if (!photoPath.isNullOrBlank()) {
                val file = File(photoPath)
                if (file.exists()) {
                    val part = MultipartBody.Part.createFormData(
                        "coverPhoto",
                        file.name,
                        file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    )
                    dto = apiService.uploadCoverPhoto(dto.id, part)
                }
            }

            Result.success(dto.toDomain())
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }

    override suspend fun updateEstablishment(
        id: Int,
        input: UpdateEstablishmentInput
    ): Result<SellerEstablishment> {
        return try {
            var dto = apiService.updateEstablishment(
                id = id,
                body = UpdateSellerEstablishmentRequestDto(
                    name = input.name.trim(),
                    category = input.category.apiKey,
                    description = input.description?.trim()?.takeIf { it.isNotEmpty() },
                    phone = input.phone.trim(),
                    address = input.address.trim(),
                    latitude = input.latitude,
                    longitude = input.longitude,
                    removeCoverPhoto = input.removeCoverPhoto,
                    businessHours = input.businessHours.map { it.toRequestDto() }
                )
            )

            val photoPath = input.localCoverPhotoPath
            if (!photoPath.isNullOrBlank()) {
                val file = File(photoPath)
                if (file.exists()) {
                    val part = MultipartBody.Part.createFormData(
                        "coverPhoto",
                        file.name,
                        file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    )
                    dto = apiService.uploadCoverPhoto(id, part)
                }
            }

            Result.success(dto.toDomain())
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }

    override suspend fun updateManualStatus(
        id: Int,
        status: ManualStatusOverride
    ): Result<SellerEstablishment> {
        return try {
            val dto = apiService.updateManualStatus(
                id = id,
                body = UpdateManualStatusRequestDto(manualStatus = status.apiKey)
            )
            Result.success(dto.toDomain())
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }
}
