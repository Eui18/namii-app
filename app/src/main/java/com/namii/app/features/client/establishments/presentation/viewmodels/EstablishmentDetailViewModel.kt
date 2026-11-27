package com.namii.app.features.client.establishments.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namii.app.features.auth.domain.usecases.ObserveSessionUseCase
import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.domain.usecases.GetEstablishmentDetailUseCase
import com.namii.app.features.client.favorites.domain.usecases.ObserveFavoriteIdsUseCase
import com.namii.app.features.client.favorites.domain.usecases.ToggleFavoriteUseCase
import com.namii.app.features.client.products.domain.entities.Product
import com.namii.app.features.client.products.domain.entities.ProductCategory
import com.namii.app.features.client.ratings.domain.usecases.RateEstablishmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EstablishmentDetailUiState(
    val establishment: Establishment? = null,
    val allProducts: List<Product> = emptyList(),
    val selectedCategory: ProductCategory? = null, // null = Todos
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = true,
    val isRatingSubmitting: Boolean = false,
    val errorMessage: String? = null
) {
    val visibleProducts: List<Product>
        get() = if (selectedCategory == null) {
            allProducts
        } else {
            allProducts.filter { it.category == selectedCategory }
        }
}

@HiltViewModel
class EstablishmentDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getEstablishmentDetailUseCase: GetEstablishmentDetailUseCase,
    private val observeSessionUseCase: ObserveSessionUseCase,
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val rateEstablishmentUseCase: RateEstablishmentUseCase
) : ViewModel() {

    private val establishmentId: Int = checkNotNull(savedStateHandle["businessId"])

    private val _uiState = MutableStateFlow(
        EstablishmentDetailUiState(isAuthenticated = observeSessionUseCase().value != null)
    )
    val uiState: StateFlow<EstablishmentDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()

        viewModelScope.launch {
            observeSessionUseCase().collect { session ->
                val wasAuth = _uiState.value.isAuthenticated
                val isNowAuth = session != null
                _uiState.update { it.copy(isAuthenticated = isNowAuth) }
                if (wasAuth != isNowAuth) {
                    loadDetail()
                }
            }
        }

        viewModelScope.launch {
            observeFavoriteIdsUseCase().collect { favIds ->
                _uiState.update { state ->
                    val current = state.establishment ?: return@update state
                    state.copy(
                        establishment = current.copy(isFavorite = favIds.contains(current.id))
                    )
                }
            }
        }
    }

    fun loadDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getEstablishmentDetailUseCase(establishmentId).fold(
                onSuccess = { detail ->
                    val favIds = observeFavoriteIdsUseCase().value
                    val est = detail.establishment.copy(
                        isFavorite = detail.establishment.isFavorite || favIds.contains(detail.establishment.id)
                    )
                    _uiState.update {
                        it.copy(
                            establishment = est,
                            allProducts = detail.products,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "No se pudo cargar el perfil del negocio"
                        )
                    }
                }
            )
        }
    }

    fun selectProductCategory(category: ProductCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun toggleFavorite(onError: (String) -> Unit) {
        viewModelScope.launch {
            toggleFavoriteUseCase(establishmentId).onFailure { err ->
                onError(err.message ?: "No se pudo actualizar favoritos")
            }
        }
    }

    fun submitRating(stars: Int, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRatingSubmitting = true) }
            rateEstablishmentUseCase(establishmentId, stars).fold(
                onSuccess = { summary ->
                    _uiState.update { state ->
                        val current = state.establishment
                        state.copy(
                            isRatingSubmitting = false,
                            establishment = current?.copy(
                                averageRating = summary.averageRating,
                                ratingCount = summary.ratingCount,
                                userRating = summary.userRating
                            )
                        )
                    }
                    onSuccess("Calificación guardada ($stars estrellas)")
                },
                onFailure = { err ->
                    _uiState.update { it.copy(isRatingSubmitting = false) }
                    onError(err.message ?: "No se pudo registrar la calificación")
                }
            )
        }
    }
}
