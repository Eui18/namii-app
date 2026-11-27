package com.namii.app.features.client.map.presentation.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.LocationOff
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.namii.app.R
import com.namii.app.core.ui.components.EstablishmentCoverVisual
import com.namii.app.core.ui.components.InlineStatusPill
import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.domain.entities.MapBounds
import com.namii.app.features.client.map.presentation.components.LocationPermissionSheet
import com.namii.app.features.client.map.presentation.viewmodels.ClientMapViewModel
import com.namii.app.features.map.presentation.components.MapPinItem
import com.namii.app.features.map.presentation.components.OsmMapView
import com.namii.app.ui.theme.AdvertenciaAmber
import com.namii.app.ui.theme.AdvertenciaSurface
import com.namii.app.ui.theme.BorderLight
import com.namii.app.ui.theme.CacaoDark
import com.namii.app.ui.theme.EstrellaAmber
import com.namii.app.ui.theme.PureWhite
import com.namii.app.ui.theme.RojoGuajillo
import com.namii.app.ui.theme.TextoMuted
import com.namii.app.ui.theme.TextoPrincipal
import com.namii.app.ui.theme.TextoSecundario
import java.util.Locale

/**
 * Vista 5. Mapa Interactivo con OpenStreetMap (osmdroid) + Hoja de permiso GPS
 */
@Composable
fun ClientMapScreen(
    onOpenDetail: (Int) -> Unit,
    onRequireAuthForFavorite: (Establishment) -> Unit,
    onShowMessage: (String) -> Unit,
    viewModel: ClientMapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
            (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
        viewModel.onSystemPermissionResult(granted)
    }

    LaunchedEffect(Unit) {
        viewModel.onPermissionStatusRefreshed()
    }

    val mapPins = remember(uiState.establishments) {
        uiState.establishments.map { est ->
            MapPinItem(
                id = est.id,
                title = est.name,
                latitude = est.latitude,
                longitude = est.longitude,
                categoryColorInt = est.category.colorInt
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OsmMapView(
            initialLat = uiState.initialLat,
            initialLng = uiState.initialLng,
            initialZoom = 15.2,
            cameraCommand = uiState.cameraCommand,
            pins = mapPins,
            selectedPinId = uiState.selectedEstablishment?.id,
            userLocation = uiState.userLocation,
            isInteractive = true,
            onPinClick = { pin -> viewModel.selectEstablishmentById(pin.id) },
            onVisibleRegionChanged = { region ->
                viewModel.onVisibleRegionChanged(
                    MapBounds(
                        north = region.north,
                        south = region.south,
                        east = region.east,
                        west = region.west
                    )
                )
            },
            modifier = Modifier.fillMaxSize()
        )

        // Buscador flotante + Aviso ámbar sin permiso + Indicador de carga discreto
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = PureWhite,
                border = BorderStroke(1.dp, BorderLight),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_placeholder),
                            fontSize = 13.sp,
                            color = TextoSecundario
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = CacaoDark,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = null,
                                    tint = CacaoDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PureWhite,
                        unfocusedContainerColor = PureWhite,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (!uiState.hasLocationPermission) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AdvertenciaSurface,
                    border = BorderStroke(1.dp, AdvertenciaAmber),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.onRequestLocationClick() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocationOff,
                            contentDescription = null,
                            tint = AdvertenciaAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.map_amber_hint),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CacaoDark
                        )
                    }
                }
            }

            if (uiState.isLoadingArea) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PureWhite.copy(alpha = 0.94f),
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        CircularProgressIndicator(
                            color = CacaoDark,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.map_updating),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextoPrincipal
                        )
                    }
                }
            }
        }

        // Controles inferiores: FAB "Mi ubicación" + Tarjeta resumen del marcador seleccionado
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = PureWhite,
                border = BorderStroke(1.dp, BorderLight),
                shadowElevation = 4.dp,
                modifier = Modifier.size(48.dp)
            ) {
                IconButton(
                    onClick = { viewModel.onRequestLocationClick() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MyLocation,
                        contentDescription = null,
                        tint = CacaoDark
                    )
                }
            }

            uiState.selectedEstablishment?.let { selected ->
                Spacer(modifier = Modifier.height(12.dp))
                MapBusinessBottomCard(
                    establishment = selected,
                    onClose = { viewModel.selectEstablishmentById(null) },
                    onFavoriteClick = {
                        if (viewModel.isAuthenticated) {
                            viewModel.toggleFavorite(selected.id, onShowMessage)
                        } else {
                            onRequireAuthForFavorite(selected)
                        }
                    },
                    onOpenProfile = { onOpenDetail(selected.id) }
                )
            }
        }
    }

    if (uiState.showPermissionSheet) {
        LocationPermissionSheet(
            onAllowClick = {
                viewModel.dismissPermissionSheet()
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            onDismissClick = { viewModel.dismissPermissionSheet() }
        )
    }
}

@Composable
private fun MapBusinessBottomCard(
    establishment: Establishment,
    onClose: () -> Unit,
    onFavoriteClick: () -> Unit,
    onOpenProfile: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                EstablishmentCoverVisual(
                    imageUrl = establishment.coverPhotoUrl,
                    fallbackColor = establishment.category.color,
                    contentDescription = establishment.name,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(14.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = establishment.name,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onFavoriteClick, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = if (establishment.isFavorite) {
                                    Icons.Rounded.Favorite
                                } else {
                                    Icons.Rounded.FavoriteBorder
                                },
                                contentDescription = null,
                                tint = if (establishment.isFavorite) RojoGuajillo else TextoMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = null,
                                tint = TextoMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = establishment.category.icon,
                            contentDescription = null,
                            tint = establishment.category.color,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = establishment.category.displayName,
                            fontSize = 12.sp,
                            color = TextoSecundario
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        InlineStatusPill(isOpen = establishment.isOpen)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = EstrellaAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format(
                                Locale.US,
                                "%.1f (%d calificaciones)",
                                establishment.averageRating,
                                establishment.ratingCount
                            ),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onOpenProfile,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CacaoDark,
                    contentColor = PureWhite
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text(
                    text = stringResource(R.string.btn_view_profile_menu),
                    color = PureWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
