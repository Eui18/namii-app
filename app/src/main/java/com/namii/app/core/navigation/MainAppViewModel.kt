package com.namii.app.core.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namii.app.features.auth.domain.usecases.ObserveSessionUseCase
import com.namii.app.features.auth.presentation.viewmodels.PendingAuthAction
import com.namii.app.features.client.favorites.domain.usecases.GetFavoritesUseCase
import com.namii.app.features.client.favorites.domain.usecases.ObserveFavoriteIdsUseCase
import com.namii.app.features.client.favorites.domain.usecases.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainAppViewModel @Inject constructor(
    observeSessionUseCase: ObserveSessionUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _pendingRating = MutableStateFlow<Pair<Int, Int>?>(null) // establishmentId to stars
    val pendingRating: StateFlow<Pair<Int, Int>?> = _pendingRating.asStateFlow()

    init {
        viewModelScope.launch {
            observeSessionUseCase().collect { session ->
                if (session != null) {
                    getFavoritesUseCase()
                }
            }
        }
    }

    fun handleAuthCompleted(action: PendingAuthAction?, onMessage: (String) -> Unit) {
        viewModelScope.launch {
            getFavoritesUseCase()
            when (action) {
                is PendingAuthAction.ToggleFavorite -> {
                    val alreadySaved = observeFavoriteIdsUseCase().value.contains(action.establishmentId)
                    if (!alreadySaved) {
                        toggleFavoriteUseCase(action.establishmentId).fold(
                            onSuccess = {
                                onMessage("${action.establishmentName} guardado en favoritos")
                            },
                            onFailure = { err ->
                                onMessage(err.message ?: "No se pudo guardar en favoritos")
                            }
                        )
                    } else {
                        onMessage("${action.establishmentName} ya estaba en tus favoritos")
                    }
                }
                is PendingAuthAction.RateEstablishment -> {
                    _pendingRating.value = action.establishmentId to action.stars
                }
                PendingAuthAction.OpenFavorites, null -> Unit
            }
        }
    }

    fun consumePendingRating() {
        _pendingRating.value = null
    }
}
