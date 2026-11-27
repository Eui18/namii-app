package com.namii.app.features.seller.establishments.domain.repositories

import com.namii.app.features.seller.establishments.domain.entities.CreateEstablishmentInput
import com.namii.app.features.seller.establishments.domain.entities.ManualStatusOverride
import com.namii.app.features.seller.establishments.domain.entities.SellerEstablishment
import com.namii.app.features.seller.establishments.domain.entities.UpdateEstablishmentInput

interface SellerEstablishmentRepository {
    suspend fun getMyEstablishment(): Result<SellerEstablishment?>
    suspend fun createEstablishment(input: CreateEstablishmentInput): Result<SellerEstablishment>
    suspend fun updateEstablishment(id: Int, input: UpdateEstablishmentInput): Result<SellerEstablishment>
    suspend fun updateManualStatus(id: Int, status: ManualStatusOverride): Result<SellerEstablishment>
}
