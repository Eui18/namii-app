package com.namii.app.features.seller.dashboard.presentation.screens

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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.namii.app.core.ui.components.ErrorStateBox
import com.namii.app.core.ui.components.LoadingStateBox
import com.namii.app.features.seller.dashboard.presentation.viewmodels.SellerDashboardViewModel
import com.namii.app.features.seller.dashboard.presentation.viewmodels.SellerEntryDestination
import com.namii.app.features.seller.dashboard.presentation.viewmodels.SellerEntryViewModel
import com.namii.app.ui.theme.BorderLight
import com.namii.app.ui.theme.DuraznoClear
import com.namii.app.ui.theme.DuraznoLight
import com.namii.app.ui.theme.ErrorRed
import com.namii.app.ui.theme.ErrorSurface
import com.namii.app.ui.theme.EstrellaAmber
import com.namii.app.ui.theme.ExitoGreen
import com.namii.app.ui.theme.ExitoSurface
import com.namii.app.ui.theme.NaranjaTostado
import com.namii.app.ui.theme.PureWhite
import com.namii.app.ui.theme.RosaGuayaba
import com.namii.app.ui.theme.SellerAmberMiel
import com.namii.app.ui.theme.SellerCacaoInk
import com.namii.app.ui.theme.SellerSoftBrown
import com.namii.app.ui.theme.TextoMuted
import java.util.Locale

@Composable
fun SellerEntryResolverScreen(
    onGoToDashboard: () -> Unit,
    onGoToOnboarding: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: SellerEntryViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsStateWithLifecycle()

    LaunchedEffect(destination) {
        when (destination) {
            SellerEntryDestination.Dashboard -> onGoToDashboard()
            SellerEntryDestination.Onboarding -> onGoToOnboarding()
            else -> Unit
        }
    }

    when (val state = destination) {
        is SellerEntryDestination.Error -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(PureWhite)
            ) {
                ErrorStateBox(
                    message = state.message,
                    onRetry = viewModel::resolveSellerEntry
                )
                TextButton(onClick = { viewModel.logout(onLoggedOut) }) {
                    Text(
                        text = "Cerrar sesión",
                        color = RosaGuayaba,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PureWhite)
            ) {
                LoadingStateBox()
            }
        }
    }
}

/**
 * V-06: Panel de control principal del vendedor.
 * Muestra estado Abierto/Cerrado (diferenciando automático vs manual), calificación promedio + votos,
 * y accesos a Gestión de Menú (V-07) y Edición del Negocio (V-10).
 * Sin conteo de favoritos ni botón de vista previa de cliente.
 */
@Composable
fun SellerDashboardScreen(
    onOpenMenu: (establishmentId: Int) -> Unit,
    onOpenEditEstablishment: () -> Unit,
    onLoggedOut: () -> Unit,
    onShowMessage: (String) -> Unit,
    viewModel: SellerDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Column {
                Text(
                    text = "Panel del vendedor",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaranjaTostado
                )
                Text(
                    text = uiState.establishment?.name ?: "Mi negocio",
                    style = MaterialTheme.typography.titleLarge,
                    color = SellerCacaoInk
                )
            }
            IconButton(onClick = { viewModel.logout(onLoggedOut) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Logout,
                    contentDescription = "Cerrar sesión",
                    tint = SellerSoftBrown
                )
            }
        }

        when {
            uiState.isLoading && uiState.establishment == null -> LoadingStateBox()
            uiState.errorMessage != null && uiState.establishment == null -> {
                ErrorStateBox(
                    message = uiState.errorMessage!!,
                    onRetry = viewModel::loadDashboard
                )
            }
            uiState.establishment != null -> {
                val est = uiState.establishment!!
                val rating = uiState.ratingSummary

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 24.dp)
                ) {
                    // Portada 16:9 (o tarjeta visual de categoría si aún no se agrega foto)
                    if (!est.coverPhotoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = est.coverPhotoUrl,
                            contentDescription = est.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(20.dp))
                        )
                    } else {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = est.category.softColor,
                            border = BorderStroke(1.dp, BorderLight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clickable(onClick = onOpenEditEstablishment)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Storefront,
                                    contentDescription = null,
                                    tint = est.category.color,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Sin foto de portada aún",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SellerCacaoInk
                                )
                                Text(
                                    text = "Toca para agregar una portada en Editar negocio",
                                    fontSize = 11.sp,
                                    color = SellerSoftBrown
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        color = est.category.softColor,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = est.category.displayName,
                            color = est.category.color,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tarjeta de Estado Abierto / Cerrado (Automático vs Manual)
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (est.isOpen) ExitoSurface.copy(alpha = 0.5f) else ErrorSurface.copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (est.isOpen) "Negocio Abierto" else "Negocio Cerrado",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (est.isOpen) ExitoGreen else ErrorRed
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.Schedule,
                                            contentDescription = null,
                                            tint = SellerSoftBrown,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = if (est.isManuallyOverridden) {
                                                "Cambio manual activo para hoy"
                                            } else {
                                                "Según tu horario automático"
                                            },
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = SellerSoftBrown
                                        )
                                    }
                                }

                                if (uiState.isTogglingStatus) {
                                    CircularProgressIndicator(
                                        color = NaranjaTostado,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Switch(
                                        checked = est.isOpen,
                                        onCheckedChange = { checked ->
                                            viewModel.toggleOpenClosed(checked, onShowMessage)
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = PureWhite,
                                            checkedTrackColor = ExitoGreen,
                                            uncheckedThumbColor = PureWhite,
                                            uncheckedTrackColor = TextoMuted
                                        )
                                    )
                                }
                            }

                            if (est.isManuallyOverridden) {
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(
                                    onClick = { viewModel.restoreAutomaticSchedule(onShowMessage) },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text(
                                        text = "Volver a horario automático",
                                        color = NaranjaTostado,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tarjeta de Calificaciones (sin conteo de favoritos)
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = DuraznoLight,
                        border = BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = SellerAmberMiel.copy(alpha = 0.22f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.Star,
                                        contentDescription = null,
                                        tint = EstrellaAmber,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Calificación de clientes",
                                    fontSize = 12.sp,
                                    color = SellerSoftBrown
                                )
                                Text(
                                    text = if ((rating?.ratingCount ?: 0) > 0) {
                                        String.format(
                                            Locale.US,
                                            "%.1f ★ · %d calificaciones",
                                            rating?.averageRating ?: 0.0,
                                            rating?.ratingCount ?: 0
                                        )
                                    } else {
                                        "Aún sin calificaciones"
                                    },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SellerCacaoInk
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    DashboardActionCard(
                        icon = Icons.Rounded.RestaurantMenu,
                        title = "Administrar menú",
                        subtitle = "Agrega, edita o marca productos como agotados",
                        onClick = { onOpenMenu(est.id) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DashboardActionCard(
                        icon = Icons.Rounded.Edit,
                        title = "Editar establecimiento",
                        subtitle = "Actualiza portada, teléfono, ubicación y horarios",
                        onClick = onOpenEditEstablishment
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DuraznoClear,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = NaranjaTostado,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SellerCacaoInk
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = SellerSoftBrown
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = NaranjaTostado,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
