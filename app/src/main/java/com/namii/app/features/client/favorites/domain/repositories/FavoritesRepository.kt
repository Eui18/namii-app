package com.namii.app.features.client.favorites.domain.repositories

import com.namii.app.features.client.establishments.domain.entities.Establishment
import kotlinx.coroutines.flow.StateFlow

interface FavoritesRepository {
    val favoriteIdsFlow: StateFlow<Set<Int>>
    suspend fun getFavorites(): Result<List<Establishment>>
    suspend fun toggleFavorite(establishmentId: Int): Result<Boolean>
    fun clearLocalCache()
}
