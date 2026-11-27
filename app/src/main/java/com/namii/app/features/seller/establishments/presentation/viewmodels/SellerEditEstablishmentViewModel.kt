package com.namii.app.features.seller.establishments.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namii.app.core.hardware.gps.domain.GpsRepository
import com.namii.app.features.client.establishments.domain.entities.EstablishmentCategory
import com.namii.app.features.map.presentation.components.MapCameraCommand
import com.namii.app.features.seller.establishments.domain.entities.DailySchedule
import com.namii.app.features.seller.establishments.domain.entities.SellerEstablishmentValidators
import com.namii.app.features.seller.establishments.domain.entities.UpdateEstablishmentInput
import com.namii.app.features.seller.establishments.domain.usecases.GetMyEstablishmentUseCase
import com.namii.app.features.seller.establishments.domain.usecases.UpdateEstablishmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SellerEditEstablishmentUiState(
    val establishmentId: Int? = null,
    val name: String = "",
    val category: EstablishmentCategory = EstablishmentCategory.RESTAURANTES,
    val description: String = "",
    val phone: String = "",
    val address: String = "",
    val latitude: Double = 16.7528,
    val longitude: Double = -93.1158,
    val cameraCommand: MapCameraCommand? = null,
    val hasLocationPermission: Boolean = false,
    val isLocatingGps: Boolean = false,
    val existingCoverPhotoUrl: String? = null,
    val localCoverPhotoPath: String? = null,
    val removeCoverPhoto: Boolean = false,
    val schedules: List<DailySchedule> = DailySchedule.defaultWeek(),
    val nameError: String? = null,
    val descriptionError: String? = null,
    val phoneError: String? = null,
    val addressError: String? = null,
    val scheduleError: String? = null,
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val submitError: String? = null,
    val isSubmitting: Boolean = false
)

@HiltViewModel
class SellerEditEstablishmentViewModel @Inject constructor(
    private val getMyEstablishmentUseCase: GetMyEstablishmentUseCase,
    private val updateEstablishmentUseCase: UpdateEstablishmentUseCase,
    private val gpsRepository: GpsRepository
) : ViewModel() {

    private var cameraRequestCounter: Long = 0L

    private val _uiState = MutableStateFlow(
        SellerEditEstablishmentUiState(
            hasLocationPermission = gpsRepository.hasLocationPermission()
        )
    )
    val uiState: StateFlow<SellerEditEstablishmentUiState> = _uiState.asStateFlow()

    init {
        loadEstablishment()
    }

    fun loadEstablishment() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadError = null) }
            getMyEstablishmentUseCase().fold(
                onSuccess = { est ->
                    if (est == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                loadError = "No se encontró el establecimiento a editar"
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                establishmentId = est.id,
                                name = est.name,
                                category = est.category,
                                description = est.description.orEmpty(),
                                phone = est.phone,
                                address = est.address,
                                latitude = est.latitude,
                                longitude = est.longitude,
                                cameraCommand = MapCameraCommand(
                                    latitude = est.latitude,
                                    longitude = est.longitude,
                                    zoomLevel = 16.0,
                                    animate = false,
                                    requestId = ++cameraRequestCounter
                                ),
                                existingCoverPhotoUrl = est.coverPhotoUrl,
                                localCoverPhotoPath = null,
                                removeCoverPhoto = false,
                                schedules = est.businessHours,
                                isLoading = false,
                                loadError = null
                            )
                        }
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadError = err.message ?: "No se pudieron cargar los datos del negocio"
                        )
                    }
                }
            )
        }
    }

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(name = value, nameError = null, submitError = null) }
    }

    fun onCategorySelected(category: EstablishmentCategory) {
        _uiState.update { it.copy(category = category, submitError = null) }
    }

    fun onDescriptionChanged(value: String) {
        _uiState.update { it.copy(description = value, descriptionError = null, submitError = null) }
    }

    fun onPhoneChanged(value: String) {
        _uiState.update { it.copy(phone = value, phoneError = null, submitError = null) }
    }

    fun onAddressChanged(value: String) {
        _uiState.update { it.copy(address = value, addressError = null, submitError = null) }
    }

    fun onCoverPhotoSelected(localPath: String) {
        _uiState.update {
            it.copy(localCoverPhotoPath = localPath, removeCoverPhoto = false, submitError = null)
        }
    }

    fun onRemoveCoverPhoto() {
        _uiState.update {
            it.copy(
                localCoverPhotoPath = null,
                existingCoverPhotoUrl = null,
                removeCoverPhoto = true,
                submitError = null
            )
        }
    }

    fun onPickerLocationChanged(lat: Double, lng: Double) {
        _uiState.update { it.copy(latitude = lat, longitude = lng, submitError = null) }
    }

    fun onPermissionStatusChanged(granted: Boolean) {
        _uiState.update { it.copy(hasLocationPermission = granted) }
        if (granted) {
            centerOnCurrentDeviceGps()
        }
    }

    fun centerOnCurrentDeviceGps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLocatingGps = true) }
            gpsRepository.getSingleLocation().fold(
                onSuccess = { loc ->
                    _uiState.update { state ->
                        state.copy(
                            latitude = loc.latitude,
                            longitude = loc.longitude,
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
                onFailure = {
                    _uiState.update { it.copy(isLocatingGps = false) }
                }
            )
        }
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
                    if (it.dayOfWeek == dayOfWeek) it.copy(opensAt = opensAt, closesAt = closesAt) else it
                },
                scheduleError = null,
                submitError = null
            )
        }
    }

    fun saveChanges(onSuccess: () -> Unit) {
        val state = _uiState.value
        val id = state.establishmentId ?: return
        val nameErr = SellerEstablishmentValidators.validateBusinessName(state.name)
        val descErr = SellerEstablishmentValidators.validateDescription(state.description)
        val phoneErr = SellerEstablishmentValidators.validateBusinessPhone(state.phone)
        val addrErr = SellerEstablishmentValidators.validateAddress(state.address)
        val schedErr = SellerEstablishmentValidators.validateSchedules(state.schedules)

        if (nameErr != null || descErr != null || phoneErr != null || addrErr != null || schedErr != null) {
            _uiState.update {
                it.copy(
                    nameError = nameErr,
                    descriptionError = descErr,
                    phoneError = phoneErr,
                    addressError = addrErr,
                    scheduleError = schedErr,
                    submitError = schedErr
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submitError = null) }
            updateEstablishmentUseCase(
                id = id,
                input = UpdateEstablishmentInput(
                    name = state.name,
                    category = state.category,
                    description = state.description,
                    phone = state.phone,
                    address = state.address,
                    latitude = state.latitude,
                    longitude = state.longitude,
                    localCoverPhotoPath = state.localCoverPhotoPath,
                    removeCoverPhoto = state.removeCoverPhoto,
                    businessHours = state.schedules
                )
            ).fold(
                onSuccess = {
                    _uiState.update { it.copy(isSubmitting = false) }
                    onSuccess()
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            submitError = err.message ?: "No se pudieron guardar los cambios"
                        )
                    }
                }
            )
        }
    }
}
