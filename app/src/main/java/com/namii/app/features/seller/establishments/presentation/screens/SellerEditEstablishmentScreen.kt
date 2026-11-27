package com.namii.app.features.seller.establishments.presentation.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.namii.app.core.ui.components.ErrorStateBox
import com.namii.app.core.ui.components.LoadingStateBox
import com.namii.app.core.ui.components.rememberPhotoPickerController
import com.namii.app.features.seller.establishments.presentation.components.DailyScheduleEditorList
import com.namii.app.features.seller.establishments.presentation.components.SellerCategoryGrid
import com.namii.app.features.seller.establishments.presentation.viewmodels.SellerEditEstablishmentViewModel
import com.namii.app.ui.theme.BorderLight
import com.namii.app.ui.theme.DuraznoClear
import com.namii.app.ui.theme.DuraznoLight
import com.namii.app.ui.theme.ErrorRed
import com.namii.app.ui.theme.ErrorSurface
import com.namii.app.ui.theme.NaranjaTostado
import com.namii.app.ui.theme.PureWhite
import com.namii.app.ui.theme.SellerCacaoInk
import com.namii.app.ui.theme.SellerSoftBrown
import java.io.File
import java.util.Locale

/**
 * V-10: Editar establecimiento (Portada cámara/galería + vista previa, información general,
 * acceso a editor de ubicación en mapa y editor de horario por día 24h).
 */
@Composable
fun SellerEditEstablishmentScreen(
    viewModel: SellerEditEstablishmentViewModel,
    onBack: () -> Unit,
    onOpenEditLocation: () -> Unit,
    onOpenEditHours: () -> Unit,
    onSaved: () -> Unit,
    onShowMessage: (String) -> Unit
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Atrás",
                    tint = SellerCacaoInk
                )
            }
            Text(
                text = "Editar establecimiento",
                style = MaterialTheme.typography.titleLarge,
                color = SellerCacaoInk
            )
        }

        when {
            state.isLoading -> LoadingStateBox()
            state.loadError != null -> ErrorStateBox(
                message = state.loadError!!,
                onRetry = viewModel::loadEstablishment
            )
            else -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 8.dp)
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
                        style = MaterialTheme.typography.labelSmall,
                        color = SellerSoftBrown
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val photoModel: Any? = state.localCoverPhotoPath?.let { File(it) }
                        ?: state.existingCoverPhotoUrl?.takeIf { it.isNotBlank() }

                    if (photoModel != null) {
                        AsyncImage(
                            model = photoModel,
                            contentDescription = state.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(18.dp))
                                .clickable {
                                    photoPicker.launch(
                                        title = "Vista previa de portada",
                                        allowRemove = true,
                                        onRemove = { viewModel.onRemoveCoverPhoto() }
                                    )
                                }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    photoPicker.launch(
                                        title = "Vista previa de portada",
                                        allowRemove = true,
                                        onRemove = { viewModel.onRemoveCoverPhoto() }
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
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = DuraznoLight,
                            border = BorderStroke(1.5.dp, BorderLight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clickable {
                                    photoPicker.launch(title = "Vista previa de portada")
                                }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AddAPhoto,
                                    contentDescription = null,
                                    tint = NaranjaTostado,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Agregar foto de portada",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SellerCacaoInk
                                )
                                Text(
                                    text = "Cámara o galería con vista previa",
                                    fontSize = 11.sp,
                                    color = SellerSoftBrown
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    SellerFormTextField(
                        label = "Nombre del negocio *",
                        value = state.name,
                        onValueChange = viewModel::onNameChanged,
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
                        error = state.descriptionError,
                        singleLine = false,
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SellerFormTextField(
                        label = "Teléfono de contacto *",
                        value = state.phone,
                        onValueChange = viewModel::onPhoneChanged,
                        error = state.phoneError,
                        keyboardType = KeyboardType.Phone
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SellerFormTextField(
                        label = "Dirección / referencia *",
                        value = state.address,
                        onValueChange = viewModel::onAddressChanged,
                        error = state.addressError
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    EditNavigationSubCard(
                        icon = Icons.Rounded.LocationOn,
                        title = "Ubicación en el mapa",
                        subtitle = String.format(
                            Locale.US,
                            "Pin en %.5f, %.5f · Toca para ajustar",
                            state.latitude,
                            state.longitude
                        ),
                        onClick = onOpenEditLocation
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val openDaysCount = state.schedules.count { !it.isClosed }
                    EditNavigationSubCard(
                        icon = Icons.Rounded.Schedule,
                        title = "Horarios por día (24h)",
                        subtitle = "$openDaysCount días activos configurados · Toca para editar",
                        onClick = onOpenEditHours
                    )

                    Spacer(modifier = Modifier.height(22.dp))
                }

                Surface(
                    color = PureWhite,
                    shadowElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { viewModel.saveChanges(onSaved) },
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
                                text = "Guardar cambios",
                                color = PureWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SellerEditLocationScreen(
    viewModel: SellerEditEstablishmentViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Atrás",
                    tint = SellerCacaoInk
                )
            }
            Text(
                text = "Ajustar ubicación en mapa",
                style = MaterialTheme.typography.titleLarge,
                color = SellerCacaoInk
            )
        }

        SellerLocationPickerContent(
            latitude = state.latitude,
            longitude = state.longitude,
            pickerColorInt = state.category.colorInt,
            cameraCommand = state.cameraCommand,
            isLocatingGps = state.isLocatingGps,
            hasLocationPermission = state.hasLocationPermission,
            errorMessage = null,
            onPickerLocationChanged = viewModel::onPickerLocationChanged,
            onUseCurrentLocation = viewModel::centerOnCurrentDeviceGps,
            onPermissionResult = viewModel::onPermissionStatusChanged,
            confirmButtonText = "Aplicar nueva ubicación",
            onConfirmClick = onBack,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SellerEditHoursScreen(
    viewModel: SellerEditEstablishmentViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Atrás",
                    tint = SellerCacaoInk
                )
            }
            Text(
                text = "Editar horarios por día",
                style = MaterialTheme.typography.titleLarge,
                color = SellerCacaoInk
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            DailyScheduleEditorList(
                schedules = state.schedules,
                onToggleDayClosed = viewModel::onToggleDayClosed,
                onUpdateTime = viewModel::onUpdateDayTime
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Surface(
            color = PureWhite,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onBack,
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
                    text = "Confirmar horarios",
                    color = PureWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EditNavigationSubCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DuraznoLight,
        border = BorderStroke(1.dp, BorderLight),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DuraznoClear,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = NaranjaTostado,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SellerCacaoInk
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = SellerSoftBrown
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = NaranjaTostado,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
