package com.namii.app.features.seller.establishments.domain.usecases

import com.namii.app.features.seller.establishments.domain.entities.CreateEstablishmentInput
import com.namii.app.features.seller.establishments.domain.entities.ManualStatusOverride
import com.namii.app.features.seller.establishments.domain.entities.SellerEstablishment
import com.namii.app.features.seller.establishments.domain.entities.SellerEstablishmentValidators
import com.namii.app.features.seller.establishments.domain.entities.UpdateEstablishmentInput
import com.namii.app.features.seller.establishments.domain.repositories.SellerEstablishmentRepository
import javax.inject.Inject

class GetMyEstablishmentUseCase @Inject constructor(
    private val repository: SellerEstablishmentRepository
) {
    suspend operator fun invoke(): Result<SellerEstablishment?> = repository.getMyEstablishment()
}

class CreateEstablishmentUseCase @Inject constructor(
    private val repository: SellerEstablishmentRepository
) {
    suspend operator fun invoke(input: CreateEstablishmentInput): Result<SellerEstablishment> {
        SellerEstablishmentValidators.validateBusinessName(input.name)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        SellerEstablishmentValidators.validateBusinessPhone(input.phone)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        SellerEstablishmentValidators.validateAddress(input.address)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        SellerEstablishmentValidators.validateDescription(input.description)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        SellerEstablishmentValidators.validateCoordinates(input.latitude, input.longitude)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        SellerEstablishmentValidators.validateSchedules(input.businessHours)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        return repository.createEstablishment(input)
    }
}

class UpdateEstablishmentUseCase @Inject constructor(
    private val repository: SellerEstablishmentRepository
) {
    suspend operator fun invoke(id: Int, input: UpdateEstablishmentInput): Result<SellerEstablishment> {
        SellerEstablishmentValidators.validateBusinessName(input.name)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        SellerEstablishmentValidators.validateBusinessPhone(input.phone)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        SellerEstablishmentValidators.validateAddress(input.address)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        SellerEstablishmentValidators.validateDescription(input.description)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        SellerEstablishmentValidators.validateCoordinates(input.latitude, input.longitude)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        SellerEstablishmentValidators.validateSchedules(input.businessHours)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        return repository.updateEstablishment(id, input)
    }
}

class ToggleEstablishmentManualStatusUseCase @Inject constructor(
    private val repository: SellerEstablishmentRepository
) {
    suspend operator fun invoke(
        establishmentId: Int,
        targetStatus: ManualStatusOverride
    ): Result<SellerEstablishment> {
        return repository.updateManualStatus(establishmentId, targetStatus)
    }
}
