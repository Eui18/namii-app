package com.namii.app.features.seller.establishments.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namii.app.core.hardware.gps.domain.GpsRepository
import com.namii.app.features.client.establishments.domain.entities.EstablishmentCategory
import com.namii.app.features.map.presentation.components.MapCameraCommand
import com.namii.app.features.seller.establishments.domain.entities.CreateEstablishmentInput
import com.namii.app.features.seller.establishments.domain.entities.DailySchedule
import com.namii.app.features.seller.establishments.domain.entities.SellerEstablishmentValidators
import com.namii.app.features.seller.establishments.domain.usecases.CreateEstablishmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DEFAULT_SELLER_LAT = 16.7528
private const val DEFAULT_SELLER_LNG = -93.1158

data class SellerOnboardingUiState(
    val name: String = "",
    val category: EstablishmentCategory = EstablishmentCategory.RESTAURANTES,
    val description: String = "",
    val phone: String = "",
    val address: String = "",
    val latitude: Double = DEFAULT_SELLER_LAT,
    val longitude: Double = DEFAULT_SELLER_LNG,
    val hasExplicitLocationSelected: Boolean = false,
    val cameraCommand: MapCameraCommand? = null,
    val hasLocationPermission: Boolean = false,
    val isLocatingGps: Boolean = false,
    val localCoverPhotoPath: String? = null,
    val schedules: List<DailySchedule> = DailySchedule.defaultWeek(),
    val nameError: String? = null,
    val descriptionError: String? = null,
    val phoneError: String? = null,
    val addressError: String? = null,
    val locationError: String? = null,
    val scheduleError: String? = null,
    val submitError: String? = null,
    val isSubmitting: Boolean = false
)

@HiltViewModel
class SellerOnboardingViewModel @Inject constructor(
    private val createEstablishmentUseCase: CreateEstablishmentUseCase,
    private val gpsRepository: GpsRepository
) : ViewModel() {

    private var cameraRequestCounter: Long = 0L

    private val _uiState = MutableStateFlow(
        SellerOnboardingUiState(
            hasLocationPermission = gpsRepository.hasLocationPermission()
        )
    )
    val uiState: StateFlow<SellerOnboardingUiState> = _uiState.asStateFlow()

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(name = value, nameError = null, submitError = null) }
    }

    fun onCategorySelected(category: EstablishmentCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun onDescriptionChanged(value: String) {
        _uiState.update { it.copy(description = value, descriptionError = null) }
    }

    fun onPhoneChanged(value: String) {
        _uiState.update { it.copy(phone = value, phoneError = null) }
    }

    fun onAddressChanged(value: String) {
        _uiState.update { it.copy(address = value, addressError = null) }
    }

    fun validateStep1(): Boolean {
        val state = _uiState.value
        val nameErr = SellerEstablishmentValidators.validateBusinessName(state.name)
        val descErr = SellerEstablishmentValidators.validateDescription(state.description)
        val phoneErr = SellerEstablishmentValidators.validateBusinessPhone(state.phone)
        val addrErr = SellerEstablishmentValidators.validateAddress(state.address)

        _uiState.update {
            it.copy(
                nameError = nameErr,
                descriptionError = descErr,
                phoneError = phoneErr,
                addressError = addrErr
            )
        }
        return nameErr == null && descErr == null && phoneErr == null && addrErr == null
    }

    fun onPickerLocationChanged(lat: Double, lng: Double) {
        // Actualiza coordenadas seleccionadas sin forzar animateTo en la cámara.
        _uiState.update {
            it.copy(
                latitude = lat,
                longitude = lng,
                hasExplicitLocationSelected = true,
                locationError = null
            )
        }
    }

    fun onPermissionStatusChanged(granted: Boolean) {
        _uiState.update { it.copy(hasLocationPermission = granted) }
        if (granted) {
            centerOnCurrentDeviceGps()
        }
    }

    fun centerOnCurrentDeviceGps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLocatingGps = true, locationError = null) }
            gpsRepository.getSingleLocation().fold(
                onSuccess = { loc ->
                    _uiState.update { state ->
                        state.copy(
                            latitude = loc.latitude,
                            longitude = loc.longitude,
                            hasExplicitLocationSelected = true,
                            hasLocationPermission = true,
                            isLocatingGps = false,
                            cameraCommand = MapCameraCommand(
                                latitude = loc.latitude,
                                longitude = loc.longitude,
                                zoomLevel = 16.5,
                                animate = true,
                                requestId = ++cameraRequestCounter
                            )
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isLocatingGps = false,
                            locationError = err.message ?: "No se pudo obtener tu ubicación actual"
                        )
                    }
                }
            )
        }
    }

    fun validateStep2(): Boolean {
        val state = _uiState.value
        val coordErr = SellerEstablishmentValidators.validateCoordinates(state.latitude, state.longitude)
        _uiState.update { it.copy(locationError = coordErr) }
        return coordErr == null
    }

    fun onCoverPhotoSelected(localPath: String?) {
        _uiState.update { it.copy(localCoverPhotoPath = localPath, submitError = null) }
    }

    fun onToggleDayClosed(dayOfWeek: Int, isClosed: Boolean) {
        _uiState.update { state ->
            state.copy(
                schedules = state.schedules.map {
                    if (it.dayOfWeek == dayOfWeek) it.copy(isClosed = isClosed) else it
                },
                scheduleError = null,
                submitError = null
            )
        }
    }

    fun onUpdateDayTime(dayOfWeek: Int, opensAt: String, closesAt: String) {
        _uiState.update { state ->
            state.copy(
                schedules = state.schedules.map {
                    if (it.dayOfWeek == dayOfWeek) {
                        it.copy(opensAt = opensAt, closesAt = closesAt)
                    } else {
                        it
                    }
                },
                scheduleError = null,
                submitError = null
            )
        }
    }

    fun submitCreateEstablishment(onSuccessDashboard: () -> Unit) {
        val state = _uiState.value
        val schedErr = SellerEstablishmentValidators.validateSchedules(state.schedules)
        if (schedErr != null) {
            _uiState.update { it.copy(scheduleError = schedErr) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submitError = null) }
            createEstablishmentUseCase(
                CreateEstablishmentInput(
                    name = state.name,
                    category = state.category,
                    description = state.description,
                    phone = state.phone,
                    address = state.address,
                    latitude = state.latitude,
                    longitude = state.longitude,
                    localCoverPhotoPath = state.localCoverPhotoPath,
                    businessHours = state.schedules
                )
            ).fold(
                onSuccess = {
                    _uiState.update { it.copy(isSubmitting = false) }
                    onSuccessDashboard()
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            submitError = err.message ?: "No se pudo registrar el negocio"
                        )
                    }
                }
            )
        }
    }
}
