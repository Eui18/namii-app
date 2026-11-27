package com.namii.app.features.client.map.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namii.app.core.hardware.gps.domain.GpsRepository
import com.namii.app.features.auth.domain.usecases.ObserveSessionUseCase
import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.domain.entities.MapBounds
import com.namii.app.features.client.favorites.domain.usecases.ObserveFavoriteIdsUseCase
import com.namii.app.features.client.favorites.domain.usecases.ToggleFavoriteUseCase
import com.namii.app.features.client.map.domain.usecases.GetMapEstablishmentsUseCase
import com.namii.app.features.map.presentation.components.MapCameraCommand
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DEFAULT_MAP_LAT = 16.7528
private const val DEFAULT_MAP_LNG = -93.1158
private const val DEFAULT_MAP_ZOOM = 15.2

data class ClientMapUiState(
    val initialLat: Double = DEFAULT_MAP_LAT,
    val initialLng: Double = DEFAULT_MAP_LNG,
    val cameraCommand: MapCameraCommand? = null,
    val userLocation: Pair<Double, Double>? = null,
    val hasLocationPermission: Boolean = false,
    val showPermissionSheet: Boolean = false,
    val searchQuery: String = "",
    val establishments: List<Establishment> = emptyList(),
    val selectedEstablishment: Establishment? = null,
    val isLoadingArea: Boolean = true,
    val errorMessage: String? = null
)

@OptIn(FlowPreview::class)
@HiltViewModel
class ClientMapViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMapEstablishmentsUseCase: GetMapEstablishmentsUseCase,
    private val gpsRepository: GpsRepository,
    private val observeSessionUseCase: ObserveSessionUseCase,
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val argLat: Double? = savedStateHandle.get<String>("lat")?.toDoubleOrNull()
    private val argLng: Double? = savedStateHandle.get<String>("lng")?.toDoubleOrNull()
    private val argEstablishmentId: Int? = savedStateHandle.get<String>("businessId")?.toIntOrNull()

    private val _searchFlow = MutableStateFlow("")
    private val _boundsFlow = MutableStateFlow<MapBounds?>(null)
    private var currentBounds: MapBounds? = null
    private var cameraRequestCounter: Long = 0L

    private val _uiState = MutableStateFlow(
        ClientMapUiState(
            initialLat = argLat ?: DEFAULT_MAP_LAT,
            initialLng = argLng ?: DEFAULT_MAP_LNG,
            cameraCommand = if (argLat != null && argLng != null) {
                MapCameraCommand(
                    latitude = argLat,
                    longitude = argLng,
                    zoomLevel = 16.0,
                    animate = true,
                    requestId = ++cameraRequestCounter
                )
            } else {
                null
            },
            hasLocationPermission = gpsRepository.hasLocationPermission()
        )
    )
    val uiState: StateFlow<ClientMapUiState> = _uiState.asStateFlow()

    val isAuthenticated: Boolean
        get() = observeSessionUseCase().value != null

    init {
        if (argLat == null && gpsRepository.hasLocationPermission()) {
            centerOnCurrentDeviceLocation(animate = true)
        } else {
            loadEstablishments(bounds = null, query = null)
        }

        viewModelScope.launch {
            _searchFlow
                .debounce(350L)
                .distinctUntilChanged()
                .collectLatest { q ->
                    loadEstablishments(bounds = currentBounds, query = q)
                }
        }

        viewModelScope.launch {
            _boundsFlow
                .filterNotNull()
                .debounce(400L)
                .distinctUntilChanged()
                .collectLatest { bounds ->
                    currentBounds = bounds
                    loadEstablishments(bounds = bounds, query = _uiState.value.searchQuery)
                }
        }

        viewModelScope.launch {
            observeFavoriteIdsUseCase().collect { favIds ->
                _uiState.update { state ->
                    val updatedList = state.establishments.map {
                        it.copy(isFavorite = favIds.contains(it.id))
                    }
                    val updatedSelected = state.selectedEstablishment?.let { sel ->
                        sel.copy(isFavorite = favIds.contains(sel.id))
                    }
                    state.copy(
                        establishments = updatedList,
                        selectedEstablishment = updatedSelected
                    )
                }
            }
        }
    }

    fun onPermissionStatusRefreshed() {
        val granted = gpsRepository.hasLocationPermission()
        _uiState.update { it.copy(hasLocationPermission = granted) }
    }

    fun onRequestLocationClick() {
        if (gpsRepository.hasLocationPermission()) {
            centerOnCurrentDeviceLocation(animate = true)
        } else {
            _uiState.update { it.copy(showPermissionSheet = true) }
        }
    }

    fun dismissPermissionSheet() {
        _uiState.update { it.copy(showPermissionSheet = false) }
    }

    fun onSystemPermissionResult(granted: Boolean) {
        _uiState.update {
            it.copy(
                hasLocationPermission = granted,
                showPermissionSheet = false
            )
        }
        if (granted) {
            centerOnCurrentDeviceLocation(animate = true)
        }
    }

    fun centerOnCurrentDeviceLocation(animate: Boolean) {
        viewModelScope.launch {
            gpsRepository.getSingleLocation().fold(
                onSuccess = { loc ->
                    _uiState.update { state ->
                        state.copy(
                            cameraCommand = MapCameraCommand(
                                latitude = loc.latitude,
                                longitude = loc.longitude,
                                zoomLevel = DEFAULT_MAP_ZOOM,
                                animate = animate,
                                requestId = ++cameraRequestCounter
                            ),
                            userLocation = loc.latitude to loc.longitude,
                            hasLocationPermission = true
                        )
                    }
                    loadEstablishments(bounds = null, query = _uiState.value.searchQuery)
                },
                onFailure = {
                    loadEstablishments(bounds = currentBounds, query = _uiState.value.searchQuery)
                }
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _searchFlow.value = query
    }

    fun onVisibleRegionChanged(bounds: MapBounds) {
        _boundsFlow.value = bounds
    }

    fun selectEstablishmentById(establishmentId: Int?) {
        _uiState.update { state ->
            state.copy(
                selectedEstablishment = establishmentId?.let { id ->
                    state.establishments.firstOrNull { it.id == id }
                }
            )
        }
    }

    fun retry() {
        loadEstablishments(bounds = currentBounds, query = _uiState.value.searchQuery)
    }

    private fun loadEstablishments(bounds: MapBounds?, query: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingArea = true, errorMessage = null) }
            getMapEstablishmentsUseCase(bounds, query).fold(
                onSuccess = { list ->
                    val favIds = observeFavoriteIdsUseCase().value
                    val merged = list.map { est ->
                        est.copy(isFavorite = est.isFavorite || favIds.contains(est.id))
                    }
                    val autoSelected = if (_uiState.value.selectedEstablishment == null && argEstablishmentId != null) {
                        merged.firstOrNull { it.id == argEstablishmentId }
                    } else {
                        _uiState.value.selectedEstablishment?.let { prev ->
                            merged.firstOrNull { it.id == prev.id }
                        }
                    }
                    _uiState.update {
                        it.copy(
                            establishments = merged,
                            selectedEstablishment = autoSelected,
                            isLoadingArea = false
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isLoadingArea = false,
                            errorMessage = err.message ?: "No se pudieron cargar los establecimientos del mapa"
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
