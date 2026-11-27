package com.namii.app.features.client.establishments.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namii.app.features.auth.domain.usecases.ObserveSessionUseCase
import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.domain.entities.EstablishmentCategory
import com.namii.app.features.client.establishments.domain.usecases.GetEstablishmentsByCategoryUseCase
import com.namii.app.features.client.favorites.domain.usecases.ObserveFavoriteIdsUseCase
import com.namii.app.features.client.favorites.domain.usecases.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryEstablishmentsUiState(
    val selectedCategory: EstablishmentCategory? = null,
    val establishments: List<Establishment> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class CategoryEstablishmentsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getEstablishmentsByCategoryUseCase: GetEstablishmentsByCategoryUseCase,
    private val observeSessionUseCase: ObserveSessionUseCase,
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val initialCategoryArg: String? = savedStateHandle["categoryName"]
    private val initialCategory: EstablishmentCategory? = initialCategoryArg
        ?.takeIf { it.isNotBlank() && it != "ALL" }
        ?.let { EstablishmentCategory.fromRaw(it) }

    private val _uiState = MutableStateFlow(
        CategoryEstablishmentsUiState(selectedCategory = initialCategory)
    )
    val uiState: StateFlow<CategoryEstablishmentsUiState> = _uiState.asStateFlow()

    val isAuthenticated: Boolean
        get() = observeSessionUseCase().value != null

    init {
        loadCategory(initialCategory)
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

    fun selectCategory(category: EstablishmentCategory?) {
        if (_uiState.value.selectedCategory == category && _uiState.value.errorMessage == null) return
        loadCategory(category)
    }

    fun retry() = loadCategory(_uiState.value.selectedCategory)

    private fun loadCategory(category: EstablishmentCategory?) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(selectedCategory = category, isLoading = true, errorMessage = null)
            }
            getEstablishmentsByCategoryUseCase(category).fold(
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
                            errorMessage = err.message ?: "Error al cargar los negocios"
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
