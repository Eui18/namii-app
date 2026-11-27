package com.namii.app.features.client.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namii.app.features.auth.domain.usecases.ObserveSessionUseCase
import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.domain.usecases.GetEstablishmentsInMapUseCase
import com.namii.app.features.client.establishments.domain.usecases.GetPopularEstablishmentsUseCase
import com.namii.app.features.client.favorites.domain.usecases.ObserveFavoriteIdsUseCase
import com.namii.app.features.client.favorites.domain.usecases.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClientHomeUiState(
    val popularEstablishments: List<Establishment> = emptyList(),
    val mapPreviewEstablishments: List<Establishment> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class ClientHomeViewModel @Inject constructor(
    private val getPopularEstablishmentsUseCase: GetPopularEstablishmentsUseCase,
    private val getEstablishmentsInMapUseCase: GetEstablishmentsInMapUseCase,
    private val observeSessionUseCase: ObserveSessionUseCase,
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientHomeUiState())
    val uiState: StateFlow<ClientHomeUiState> = _uiState.asStateFlow()

    val isAuthenticated: Boolean
        get() = observeSessionUseCase().value != null

    init {
        loadHomeData()
        viewModelScope.launch {
            observeFavoriteIdsUseCase().collect { favIds ->
                _uiState.update { state ->
                    state.copy(
                        popularEstablishments = state.popularEstablishments.map {
                            it.copy(isFavorite = favIds.contains(it.id))
                        }
                    )
                }
            }
        }
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val popularDeferred = async { getPopularEstablishmentsUseCase(limit = 4) }
            val mapDeferred = async { getEstablishmentsInMapUseCase(bounds = null, query = null) }

            val popularResult = popularDeferred.await()
            val mapResult = mapDeferred.await()
            val favIds = observeFavoriteIdsUseCase().value

            popularResult.fold(
                onSuccess = { list ->
                    val merged = list.map { est ->
                        est.copy(isFavorite = est.isFavorite || favIds.contains(est.id))
                    }
                    _uiState.update {
                        it.copy(
                            popularEstablishments = merged,
                            mapPreviewEstablishments = mapResult.getOrDefault(merged),
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "No se pudieron cargar los negocios"
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
