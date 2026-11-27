package com.namii.app.features.seller.establishments.presentation.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.namii.app.core.ui.components.rememberPhotoPickerController
import com.namii.app.features.map.presentation.components.MapCameraCommand
import com.namii.app.features.map.presentation.components.OsmMapView
import com.namii.app.features.seller.establishments.presentation.components.DailyScheduleEditorList
import com.namii.app.features.seller.establishments.presentation.components.SellerCategoryGrid
import com.namii.app.features.seller.establishments.presentation.components.SellerStepHeader
import com.namii.app.features.seller.establishments.presentation.viewmodels.SellerOnboardingViewModel
import com.namii.app.ui.theme.BorderLight
import com.namii.app.ui.theme.DuraznoClear
import com.namii.app.ui.theme.DuraznoLight
import com.namii.app.ui.theme.ErrorRed
import com.namii.app.ui.theme.ErrorSurface
import com.namii.app.ui.theme.InputBackground
import com.namii.app.ui.theme.NaranjaTostado
import com.namii.app.ui.theme.PureWhite
import com.namii.app.ui.theme.SellerCacaoInk
import com.namii.app.ui.theme.SellerSoftBrown
import java.io.File
import java.util.Locale

/**
 * V-03: Registro del negocio Paso 1/3 - Información general.
 */
@Composable
fun SellerEstablishmentInfoScreen(
    viewModel: SellerOnboardingViewModel,
    onBack: () -> Unit,
    onNextStep: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
    ) {
        SellerStepHeader(
            title = "Datos de tu negocio",
            subtitle = "Cuéntanos cómo se llama tu establecimiento y en qué categoría aparece.",
            stepNumber = 1,
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            SellerFormTextField(
                label = "Nombre del negocio *",
                value = state.name,
                onValueChange = viewModel::onNameChanged,
                placeholder = "Ej. Cafetería La Ceiba",
                error = state.nameError
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Categoría *",
                style = MaterialTheme.typography.labelSmall,
                color = SellerSoftBrown
            )
            Spacer(modifier = Modifier.height(8.dp))
            SellerCategoryGrid(
                selectedCategory = state.category,
                onSelectCategory = viewModel::onCategorySelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            SellerFormTextField(
                label = "Descripción breve (opcional)",
                value = state.description,
                onValueChange = viewModel::onDescriptionChanged,
                placeholder = "Especialidades, ambiente o platillos estrella",
                error = state.descriptionError,
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            SellerFormTextField(
                label = "Teléfono del establecimiento *",
                value = state.phone,
                onValueChange = viewModel::onPhoneChanged,
                placeholder = "9611234567",
                error = state.phoneError,
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(16.dp))

            SellerFormTextField(
                label = "Dirección / referencia *",
                value = state.address,
                onValueChange = viewModel::onAddressChanged,
                placeholder = "Calle Central #123, Col. Centro",
                error = state.addressError
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        Surface(
            color = PureWhite,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    if (viewModel.validateStep1()) {
                        onNextStep()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NaranjaTostado,
                    contentColor = PureWhite
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "Continuar a ubicación",
                    color = PureWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * V-04: Paso 2/3 - Selección y ajuste manual de ubicación del negocio en OpenStreetMap.
 */
@Composable
fun SellerOnboardingLocationScreen(
    viewModel: SellerOnboardingViewModel,
    onBack: () -> Unit,
    onNextStep: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
    ) {
        SellerStepHeader(
            title = "Ubicación del negocio",
            subtitle = "Toca el mapa o arrastra el marcador exactamente a la puerta de tu local.",
            stepNumber = 2,
            onBack = onBack
        )

        SellerLocationPickerContent(
            latitude = state.latitude,
            longitude = state.longitude,
            pickerColorInt = state.category.colorInt,
            cameraCommand = state.cameraCommand,
            isLocatingGps = state.isLocatingGps,
            hasLocationPermission = state.hasLocationPermission,
            errorMessage = state.locationError,
            onPickerLocationChanged = viewModel::onPickerLocationChanged,
            onUseCurrentLocation = viewModel::centerOnCurrentDeviceGps,
            onPermissionResult = viewModel::onPermissionStatusChanged,
            confirmButtonText = "Confirmar ubicación",
            onConfirmClick = {
                if (viewModel.validateStep2()) {
                    onNextStep()
                }
            },
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Componente compartido para elegir o editar la ubicación del negocio (V-04 y V-10).
 */
@Composable
fun SellerLocationPickerContent(
    latitude: Double,
    longitude: Double,
    pickerColorInt: Int,
    cameraCommand: MapCameraCommand?,
    isLocatingGps: Boolean,
    hasLocationPermission: Boolean,
    errorMessage: String?,
    onPickerLocationChanged: (Double, Double) -> Unit,
    onUseCurrentLocation: () -> Unit,
    onPermissionResult: (Boolean) -> Unit,
    confirmButtonText: String,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = (perms[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
            (perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
        onPermissionResult(granted)
    }

    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            OsmMapView(
                initialLat = latitude,
                initialLng = longitude,
                initialZoom = 16.0,
                cameraCommand = cameraCommand,
                isInteractive = true,
                isPickerMode = true,
                pickerLocation = latitude to longitude,
                pickerColorInt = pickerColorInt,
                onPickerLocationChanged = onPickerLocationChanged,
                modifier = Modifier.fillMaxSize()
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PureWhite.copy(alpha = 0.94f),
                border = BorderStroke(1.dp, BorderLight),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.TouchApp,
                        contentDescription = null,
                        tint = NaranjaTostado,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Toca el mapa o mantén presionado el pin para moverlo",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = SellerCacaoInk
                    )
                }
            }
        }

        Surface(
            color = PureWhite,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = String.format(
                        Locale.US,
                        "Coordenadas seleccionadas: %.5f, %.5f",
                        latitude,
                        longitude
                    ),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SellerSoftBrown
                )

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = it, color = ErrorRed, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        if (hasLocationPermission) {
                            onUseCurrentLocation()
                        } else {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    enabled = !isLocatingGps,
                    border = BorderStroke(1.dp, NaranjaTostado),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    if (isLocatingGps) {
                        CircularProgressIndicator(
                            color = NaranjaTostado,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.MyLocation,
                            contentDescription = null,
                            tint = NaranjaTostado,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Usar mi ubicación actual",
                        color = NaranjaTostado,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onConfirmClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NaranjaTostado,
                        contentColor = PureWhite
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = confirmButtonText,
                        color = PureWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * V-05: Paso 3/3 - Portada (opcional con Cámara/Galería + Vista Previa) y Horario independiente por día (24h).
 */
@Composable
fun SellerCoverAndHoursScreen(
    viewModel: SellerOnboardingViewModel,
    onBack: () -> Unit,
    onShowMessage: (String) -> Unit,
    onEstablishmentCreated: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val photoPicker = rememberPhotoPickerController(
        onPhotoConfirmed = { localPath -> viewModel.onCoverPhotoSelected(localPath) },
        onPermissionDenied = onShowMessage
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
    ) {
        SellerStepHeader(
            title = "Foto y horarios de atención",
            subtitle = "La foto de portada es opcional; puedes agregarla ahora o después desde tu panel.",
            stepNumber = 3,
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            state.submitError?.let { err ->
                Surface(
                    color = ErrorSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = err,
                        color = ErrorRed,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Text(
                text = "Foto de portada (opcional)",
                style = MaterialTheme.typography.titleSmall,
                color = SellerCacaoInk
            )
            Spacer(modifier = Modifier.height(8.dp))

            val photoPath = state.localCoverPhotoPath
            if (photoPath != null) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = File(photoPath),
                        contentDescription = "Portada seleccionada",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable {
                                photoPicker.launch(
                                    title = "Vista previa de portada",
                                    allowRemove = true,
                                    onRemove = { viewModel.onCoverPhotoSelected(null) }
                                )
                            }
                    )
                    Surface(
                        color = SellerCacaoInk.copy(alpha = 0.72f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .clickable { viewModel.onCoverPhotoSelected(null) }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Quitar foto",
                            tint = PureWhite,
                            modifier = Modifier.padding(6.dp).size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        photoPicker.launch(
                            title = "Vista previa de portada",
                            allowRemove = true,
                            onRemove = { viewModel.onCoverPhotoSelected(null) }
                        )
                    },
                    border = BorderStroke(1.dp, BorderLight),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Cambiar o quitar foto",
                        color = SellerCacaoInk,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = DuraznoLight,
                    border = BorderStroke(1.5.dp, BorderLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clickable { photoPicker.launch(title = "Vista previa de portada") }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DuraznoClear,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.AddAPhoto,
                                    contentDescription = null,
                                    tint = NaranjaTostado,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Agregar foto de portada (opcional)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SellerCacaoInk
                        )
                        Text(
                            text = "Toma una foto con cámara o elige de tu galería",
                            fontSize = 11.sp,
                            color = SellerSoftBrown
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Horario por día (formato 24 horas) *",
                style = MaterialTheme.typography.titleSmall,
                color = SellerCacaoInk
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Configura cada día de forma independiente (ej. Sábado de 08:00 a 16:00).",
                fontSize = 12.sp,
                color = SellerSoftBrown
            )

            state.scheduleError?.let {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = it, color = ErrorRed, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            DailyScheduleEditorList(
                schedules = state.schedules,
                onToggleDayClosed = viewModel::onToggleDayClosed,
                onUpdateTime = viewModel::onUpdateDayTime
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        Surface(
            color = PureWhite,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { viewModel.submitCreateEstablishment(onEstablishmentCreated) },
                enabled = !state.isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NaranjaTostado,
                    contentColor = PureWhite
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .height(50.dp)
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(
                        color = PureWhite,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "Finalizar y publicar negocio",
                        color = PureWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
internal fun SellerFormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    error: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = SellerSoftBrown
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            minLines = minLines,
            isError = error != null,
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 13.sp,
                    color = SellerSoftBrown.copy(alpha = 0.6f)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                focusedBorderColor = NaranjaTostado,
                unfocusedBorderColor = BorderLight,
                errorBorderColor = ErrorRed
            ),
            modifier = Modifier.fillMaxWidth()
        )
        error?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = it, color = ErrorRed, fontSize = 11.sp)
        }
    }
}
