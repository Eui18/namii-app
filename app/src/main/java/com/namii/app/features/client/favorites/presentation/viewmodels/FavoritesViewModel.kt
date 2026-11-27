package com.namii.app.features.client.favorites.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namii.app.features.auth.domain.entities.AuthUser
import com.namii.app.features.auth.domain.usecases.LogoutClientUseCase
import com.namii.app.features.auth.domain.usecases.ObserveSessionUseCase
import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.favorites.domain.usecases.ClearFavoritesCacheUseCase
import com.namii.app.features.client.favorites.domain.usecases.GetFavoritesUseCase
import com.namii.app.features.client.favorites.domain.usecases.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val user: AuthUser? = null,
    val favorites: List<Establishment> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val observeSessionUseCase: ObserveSessionUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val logoutClientUseCase: LogoutClientUseCase,
    private val clearFavoritesCacheUseCase: ClearFavoritesCacheUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        FavoritesUiState(user = observeSessionUseCase().value?.user)
    )
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeSessionUseCase().collect { session ->
                if (session == null) {
                    _uiState.value = FavoritesUiState(user = null)
                } else {
                    _uiState.update { it.copy(user = session.user) }
                    loadFavorites()
                }
            }
        }
    }

    fun loadFavorites() {
        if (observeSessionUseCase().value == null) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getFavoritesUseCase().fold(
                onSuccess = { list ->
                    _uiState.update {
                        it.copy(
                            favorites = list,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "No se pudieron cargar tus favoritos"
                        )
                    }
                }
            )
        }
    }

    fun removeFavorite(establishmentId: Int, onError: (String) -> Unit) {
        viewModelScope.launch {
            toggleFavoriteUseCase(establishmentId).fold(
                onSuccess = {
                    _uiState.update { state ->
                        state.copy(
                            favorites = state.favorites.filterNot { it.id == establishmentId }
                        )
                    }
                },
                onFailure = { err ->
                    onError(err.message ?: "No se pudo quitar de favoritos")
                }
            )
        }
    }

    fun logout() {
        logoutClientUseCase()
        clearFavoritesCacheUseCase()
    }
}
