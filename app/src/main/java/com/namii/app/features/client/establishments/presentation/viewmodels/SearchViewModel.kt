package com.namii.app.features.client.establishments.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namii.app.features.auth.domain.usecases.ObserveSessionUseCase
import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.domain.usecases.SearchEstablishmentsUseCase
import com.namii.app.features.client.favorites.domain.usecases.ObserveFavoriteIdsUseCase
import com.namii.app.features.client.favorites.domain.usecases.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<Establishment> = emptyList(),
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false,
    val errorMessage: String? = null
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchEstablishmentsUseCase: SearchEstablishmentsUseCase,
    private val observeSessionUseCase: ObserveSessionUseCase,
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _queryFlow = MutableStateFlow("")
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    val isAuthenticated: Boolean
        get() = observeSessionUseCase().value != null

    init {
        viewModelScope.launch {
            _queryFlow
                .debounce(350L)
                .distinctUntilChanged()
                .collectLatest { q ->
                    if (q.isBlank()) {
                        _uiState.update {
                            it.copy(
                                results = emptyList(),
                                isSearching = false,
                                hasSearched = false,
                                errorMessage = null
                            )
                        }
                    } else {
                        performSearch(q)
                    }
                }
        }

        viewModelScope.launch {
            observeFavoriteIdsUseCase().collect { favIds ->
                _uiState.update { state ->
                    state.copy(
                        results = state.results.map {
                            it.copy(isFavorite = favIds.contains(it.id))
                        }
                    )
                }
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.update { it.copy(query = newQuery, errorMessage = null) }
        _queryFlow.value = newQuery
    }

    fun clearQuery() {
        onQueryChanged("")
    }

    fun retry() {
        val current = _uiState.value.query
        if (current.isNotBlank()) {
            viewModelScope.launch { performSearch(current) }
        }
    }

    private suspend fun performSearch(query: String) {
        _uiState.update { it.copy(isSearching = true, errorMessage = null) }
        searchEstablishmentsUseCase(query).fold(
            onSuccess = { list ->
                val favIds = observeFavoriteIdsUseCase().value
                _uiState.update {
                    it.copy(
                        results = list.map { est ->
                            est.copy(isFavorite = est.isFavorite || favIds.contains(est.id))
                        },
                        isSearching = false,
                        hasSearched = true
                    )
                }
            },
            onFailure = { err ->
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        hasSearched = true,
                        errorMessage = err.message ?: "Error al buscar negocios"
                    )
                }
            }
        )
    }

    fun toggleFavorite(establishmentId: Int, onError: (String) -> Unit) {
        viewModelScope.launch {
            toggleFavoriteUseCase(establishmentId).onFailure { err ->
                onError(err.message ?: "No se pudo actualizar favoritos")
            }
        }
    }
}
