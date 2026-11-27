package com.namii.app.features.seller.products.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.namii.app.core.ui.components.ErrorStateBox
import com.namii.app.core.ui.components.LoadingStateBox
import com.namii.app.core.ui.components.rememberPhotoPickerController
import com.namii.app.features.client.products.domain.entities.ProductCategory
import com.namii.app.features.seller.establishments.presentation.screens.SellerFormTextField
import com.namii.app.features.seller.products.presentation.viewmodels.SellerProductFormViewModel
import com.namii.app.ui.theme.BorderLight
import com.namii.app.ui.theme.DuraznoClear
import com.namii.app.ui.theme.DuraznoLight
import com.namii.app.ui.theme.ErrorRed
import com.namii.app.ui.theme.ErrorSurface
import com.namii.app.ui.theme.ExitoGreen
import com.namii.app.ui.theme.NaranjaTostado
import com.namii.app.ui.theme.PureWhite
import com.namii.app.ui.theme.SellerCacaoInk
import com.namii.app.ui.theme.SellerSoftBrown
import com.namii.app.ui.theme.TextoMuted
import java.io.File

/**
 * V-08: Agregar / Editar producto (con Cámara/Galería + Vista Previa obligatoria).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SellerProductFormScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onShowMessage: (String) -> Unit,
    viewModel: SellerProductFormViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val photoPicker = rememberPhotoPickerController(
        onPhotoConfirmed = { path -> viewModel.onPhotoConfirmed(path) },
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
                text = if (state.isEditMode) "Editar producto" else "Nuevo producto",
                style = MaterialTheme.typography.titleLarge,
                color = SellerCacaoInk
            )
        }

        when {
            state.isLoadingInitial -> LoadingStateBox()
            state.isEditMode && state.generalError != null && state.name.isBlank() -> {
                ErrorStateBox(
                    message = state.generalError!!,
                    onRetry = viewModel::reloadExistingProduct
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    state.generalError?.let { err ->
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
                        text = "Fotografía del producto *",
                        style = MaterialTheme.typography.labelSmall,
                        color = SellerSoftBrown
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val photoModel: Any? = state.localPhotoPath?.let { File(it) }
                        ?: state.existingPhotoUrl?.takeIf { it.isNotBlank() }

                    if (photoModel != null) {
                        AsyncImage(
                            model = photoModel,
                            contentDescription = state.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 10f)
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { photoPicker.launch(title = "Vista previa del producto") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { photoPicker.launch(title = "Vista previa del producto") },
                            border = BorderStroke(1.dp, BorderLight),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Cambiar fotografía",
                                color = SellerCacaoInk,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = DuraznoLight,
                            border = BorderStroke(
                                1.5.dp,
                                if (state.photoError != null) ErrorRed else BorderLight
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 10f)
                                .clickable { photoPicker.launch(title = "Vista previa del producto") }
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
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Subir foto del producto",
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
                    state.photoError?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = it, color = ErrorRed, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SellerFormTextField(
                        label = "Nombre del producto *",
                        value = state.name,
                        onValueChange = viewModel::onNameChanged,
                        placeholder = "Ej. Chilaquiles verdes con pollo",
                        error = state.nameError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Categoría interna *",
                        style = MaterialTheme.typography.labelSmall,
                        color = SellerSoftBrown
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ProductCategory.entries.forEach { category ->
                            val selected = state.category == category
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (selected) NaranjaTostado else DuraznoLight,
                                border = BorderStroke(1.dp, if (selected) NaranjaTostado else BorderLight),
                                modifier = Modifier.clickable { viewModel.onCategorySelected(category) }
                            ) {
                                Text(
                                    text = category.displayName,
                                    color = if (selected) PureWhite else SellerCacaoInk,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SellerFormTextField(
                        label = "Descripción breve (opcional)",
                        value = state.description,
                        onValueChange = viewModel::onDescriptionChanged,
                        placeholder = "Ingredientes o presentación",
                        error = state.descriptionError,
                        singleLine = false,
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SellerFormTextField(
                        label = "Precio (MXN) *",
                        value = state.priceText,
                        onValueChange = viewModel::onPriceChanged,
                        placeholder = "85.00",
                        error = state.priceError,
                        keyboardType = KeyboardType.Decimal
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DuraznoLight,
                        border = BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Disponible en este momento",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SellerCacaoInk
                                )
                                Text(
                                    text = if (state.isAvailable) "Se muestra como disponible" else "Se mostrará como Agotado al público",
                                    fontSize = 11.sp,
                                    color = SellerSoftBrown
                                )
                            }
                            Switch(
                                checked = state.isAvailable,
                                onCheckedChange = viewModel::onAvailabilityChanged,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = PureWhite,
                                    checkedTrackColor = ExitoGreen,
                                    uncheckedThumbColor = PureWhite,
                                    uncheckedTrackColor = TextoMuted
                                )
                            )
                        }
                    }

                    if (state.isEditMode) {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = viewModel::requestDelete,
                            border = BorderStroke(1.dp, ErrorRed),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Text(
                                text = "Eliminar producto",
                                color = ErrorRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                Surface(
                    color = PureWhite,
                    shadowElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { viewModel.saveProduct(onSaved) },
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
                                text = if (state.isEditMode) "Guardar cambios" else "Guardar producto",
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

    if (state.showDeleteConfirm) {
        DeleteProductConfirmDialog(
            productName = state.name.ifBlank { "este producto" },
            isDeleting = state.isDeleting,
            onConfirm = {
                viewModel.confirmDelete(
                    onDeleted = onSaved,
                    onError = onShowMessage
                )
            },
            onDismiss = viewModel::dismissDelete
        )
    }
}
