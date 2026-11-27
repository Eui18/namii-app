package com.namii.app.features.client.establishments.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namii.app.features.auth.domain.usecases.ObserveSessionUseCase
import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.domain.usecases.GetPopularEstablishmentsUseCase
import com.namii.app.features.client.favorites.domain.usecases.ObserveFavoriteIdsUseCase
import com.namii.app.features.client.favorites.domain.usecases.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PopularEstablishmentsUiState(
    val establishments: List<Establishment> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class PopularEstablishmentsViewModel @Inject constructor(
    private val getPopularEstablishmentsUseCase: GetPopularEstablishmentsUseCase,
    private val observeSessionUseCase: ObserveSessionUseCase,
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PopularEstablishmentsUiState())
    val uiState: StateFlow<PopularEstablishmentsUiState> = _uiState.asStateFlow()

    val isAuthenticated: Boolean
        get() = observeSessionUseCase().value != null

    init {
        loadPopular()
        viewModelScope.launch {
            observeFavoriteIdsUseCase().collect { favIds ->
                _uiState.update { state ->
                    state.copy(
                        establishments = state.establishments.map {
                            it.copy(isFavorite = favIds.contains(it.id))
                        }
                    )
                }
            }
        }
    }

    fun loadPopular() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getPopularEstablishmentsUseCase(limit = 10).fold(
                onSuccess = { list ->
                    val favIds = observeFavoriteIdsUseCase().value
                    _uiState.update {
                        it.copy(
                            establishments = list.map { est ->
                                est.copy(isFavorite = est.isFavorite || favIds.contains(est.id))
                            },
                            isLoading = false
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "No se pudieron cargar los populares"
                        )
                    }
                }
            )
        }
    }

    fun toggleFavorite(establishmentId: Int, onError: (String) -> Unit) {
        viewModelScope.launch {
            toggleFavoriteUseCase(establishmentId).onFailure { err ->
                onError(err.message ?: "No se pudo actualizar favoritos")
            }
        }
    }
}
