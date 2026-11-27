package com.namii.app.features.seller.dashboard.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namii.app.features.auth.domain.usecases.LogoutClientUseCase
import com.namii.app.features.seller.establishments.domain.entities.ManualStatusOverride
import com.namii.app.features.seller.establishments.domain.entities.SellerEstablishment
import com.namii.app.features.seller.establishments.domain.usecases.GetMyEstablishmentUseCase
import com.namii.app.features.seller.establishments.domain.usecases.ToggleEstablishmentManualStatusUseCase
import com.namii.app.features.seller.ratings.domain.BuildSellerRatingSummaryUseCase
import com.namii.app.features.seller.ratings.domain.SellerRatingSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SellerEntryDestination {
    data object Loading : SellerEntryDestination
    data object Onboarding : SellerEntryDestination
    data object Dashboard : SellerEntryDestination
    data class Error(val message: String) : SellerEntryDestination
}

@HiltViewModel
class SellerEntryViewModel @Inject constructor(
    private val getMyEstablishmentUseCase: GetMyEstablishmentUseCase,
    private val logoutUseCase: LogoutClientUseCase
) : ViewModel() {

    private val _destination = MutableStateFlow<SellerEntryDestination>(SellerEntryDestination.Loading)
    val destination: StateFlow<SellerEntryDestination> = _destination.asStateFlow()

    init {
        resolveSellerEntry()
    }

    fun resolveSellerEntry() {
        viewModelScope.launch {
            _destination.value = SellerEntryDestination.Loading
            getMyEstablishmentUseCase().fold(
                onSuccess = { est ->
                    _destination.value = if (est != null) {
                        SellerEntryDestination.Dashboard
                    } else {
                        SellerEntryDestination.Onboarding
                    }
                },
                onFailure = { err ->
                    _destination.value = SellerEntryDestination.Error(
                        err.message ?: "No se pudo verificar el estado de tu negocio"
                    )
                }
            )
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        logoutUseCase()
        onLoggedOut()
    }
}

data class SellerDashboardUiState(
    val establishment: SellerEstablishment? = null,
    val ratingSummary: SellerRatingSummary? = null,
    val isLoading: Boolean = true,
    val isTogglingStatus: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SellerDashboardViewModel @Inject constructor(
    private val getMyEstablishmentUseCase: GetMyEstablishmentUseCase,
    private val toggleManualStatusUseCase: ToggleEstablishmentManualStatusUseCase,
    private val buildRatingSummaryUseCase: BuildSellerRatingSummaryUseCase,
    private val logoutUseCase: LogoutClientUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SellerDashboardUiState())
    val uiState: StateFlow<SellerDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getMyEstablishmentUseCase().fold(
                onSuccess = { est ->
                    _uiState.update {
                        it.copy(
                            establishment = est,
                            ratingSummary = est?.let { e -> buildRatingSummaryUseCase(e) },
                            isLoading = false,
                            errorMessage = if (est == null) "No se encontró tu establecimiento registrado." else null
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "No se pudo cargar tu panel"
                        )
                    }
                }
            )
        }
    }

    fun toggleOpenClosed(targetOpen: Boolean, onError: (String) -> Unit) {
        val current = _uiState.value.establishment ?: return
        val nextManual = if (targetOpen) ManualStatusOverride.OPEN else ManualStatusOverride.CLOSED
        viewModelScope.launch {
            _uiState.update { it.copy(isTogglingStatus = true) }
            toggleManualStatusUseCase(current.id, nextManual).fold(
                onSuccess = { updated ->
                    _uiState.update {
                        it.copy(
                            establishment = updated,
                            ratingSummary = buildRatingSummaryUseCase(updated),
                            isTogglingStatus = false
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(isTogglingStatus = false) }
                    onError(err.message ?: "No se pudo actualizar el estado de apertura")
                }
            )
        }
    }

    fun restoreAutomaticSchedule(onError: (String) -> Unit) {
        val current = _uiState.value.establishment ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isTogglingStatus = true) }
            toggleManualStatusUseCase(current.id, ManualStatusOverride.NONE).fold(
                onSuccess = { updated ->
                    _uiState.update {
                        it.copy(
                            establishment = updated,
                            ratingSummary = buildRatingSummaryUseCase(updated),
                            isTogglingStatus = false
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(isTogglingStatus = false) }
                    onError(err.message ?: "No se pudo restaurar el horario automático")
                }
            )
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        logoutUseCase()
        onLoggedOut()
    }
}
