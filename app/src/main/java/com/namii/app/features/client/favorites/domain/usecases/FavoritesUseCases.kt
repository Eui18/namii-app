package com.namii.app.features.client.favorites.domain.usecases

import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.favorites.domain.repositories.FavoritesRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveFavoriteIdsUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    operator fun invoke(): StateFlow<Set<Int>> = repository.favoriteIdsFlow
}

class GetFavoritesUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(): Result<List<Establishment>> = repository.getFavorites()
}

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(establishmentId: Int): Result<Boolean> {
        return repository.toggleFavorite(establishmentId)
    }
}

class ClearFavoritesCacheUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    operator fun invoke() = repository.clearLocalCache()
}
