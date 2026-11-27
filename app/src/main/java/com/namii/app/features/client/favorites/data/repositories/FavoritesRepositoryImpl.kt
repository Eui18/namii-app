package com.namii.app.features.client.favorites.data.repositories

import com.namii.app.core.network.ApiErrorParser
import com.namii.app.features.client.establishments.data.mapper.toDomain
import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.favorites.data.datasources.remote.FavoritesApiService
import com.namii.app.features.client.favorites.domain.repositories.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val apiService: FavoritesApiService,
    private val errorParser: ApiErrorParser
) : FavoritesRepository {

    private val _favoriteIdsFlow = MutableStateFlow<Set<Int>>(emptySet())
    override val favoriteIdsFlow: StateFlow<Set<Int>> = _favoriteIdsFlow.asStateFlow()

    override suspend fun getFavorites(): Result<List<Establishment>> {
        return try {
            val list = apiService.getFavorites().map { it.toDomain().copy(isFavorite = true) }
            _favoriteIdsFlow.value = list.map { it.id }.toSet()
            Result.success(list)
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }

    override suspend fun toggleFavorite(establishmentId: Int): Result<Boolean> {
        val currentlyFavorite = _favoriteIdsFlow.value.contains(establishmentId)
        return try {
            val response = if (currentlyFavorite) {
                apiService.removeFavorite(establishmentId)
            } else {
                apiService.addFavorite(establishmentId)
            }
            if (!response.isSuccessful) {
                throw HttpException(response)
            }
            val nowFavorite = !currentlyFavorite
            _favoriteIdsFlow.value = if (nowFavorite) {
                _favoriteIdsFlow.value + establishmentId
            } else {
                _favoriteIdsFlow.value - establishmentId
            }
            Result.success(nowFavorite)
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }

    override fun clearLocalCache() {
        _favoriteIdsFlow.value = emptySet()
    }
}
