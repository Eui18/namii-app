package com.namii.app.features.client.home.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.namii.app.R
import com.namii.app.core.ui.components.BusinessGridCard
import com.namii.app.core.ui.components.EmptyStateBox
import com.namii.app.core.ui.components.ErrorStateBox
import com.namii.app.features.map.presentation.components.MapPinItem
import com.namii.app.features.map.presentation.components.OsmMapView
import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.domain.entities.EstablishmentCategory
import com.namii.app.features.client.home.presentation.viewmodels.ClientHomeViewModel
import com.namii.app.ui.theme.BorderLight
import com.namii.app.ui.theme.CacaoDark
import com.namii.app.ui.theme.DuraznoClear
import com.namii.app.ui.theme.PureWhite
import com.namii.app.ui.theme.TextoPrincipal

private const val DEFAULT_PREVIEW_LAT = 16.7528
private const val DEFAULT_PREVIEW_LNG = -93.1158

/**
 * Vista 1. Inicio
 */
@Composable
fun ClientHomeScreen(
    onOpenSearch: () -> Unit,
    onSelectCategory: (EstablishmentCategory) -> Unit,
    onOpenMap: () -> Unit,
    onSeeAllPopular: () -> Unit,
    onOpenDetail: (Int) -> Unit,
    onRequireAuthForFavorite: (Establishment) -> Unit,
    onShowMessage: (String) -> Unit,
    viewModel: ClientHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        contentPadding = PaddingValues(bottom = 24.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
    ) {
        // Encabezado + Buscador destacado
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 14.dp, bottom = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.brand_header),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = CacaoDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.home_greeting),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextoPrincipal
                )
                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    color = DuraznoClear,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable(onClick = onOpenSearch)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = CacaoDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.search_placeholder),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = CacaoDark
                        )
                    }
                }
            }
        }

        // Sección Categorías (carril horizontal sin "Ver todo")
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.section_categories),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(EstablishmentCategory.entries, key = { it.name }) { category ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(68.dp)
                            .clickable { onSelectCategory(category) }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(category.color)
                        ) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = category.displayName,
                                tint = PureWhite,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = category.displayName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextoPrincipal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Sección Explorar (Vista previa del mapa)
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.section_explore),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            val mapCenterLat = uiState.mapPreviewEstablishments.firstOrNull()?.latitude ?: DEFAULT_PREVIEW_LAT
            val mapCenterLng = uiState.mapPreviewEstablishments.firstOrNull()?.longitude ?: DEFAULT_PREVIEW_LNG

            Surface(
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, BorderLight),
                color = PureWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(152.dp)
                    .clickable(onClick = onOpenMap)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val previewPins = remember(uiState.mapPreviewEstablishments) {
                        uiState.mapPreviewEstablishments.map { est ->
                            MapPinItem(
                                id = est.id,
                                title = est.name,
                                latitude = est.latitude,
                                longitude = est.longitude,
                                categoryColorInt = est.category.colorInt
                            )
                        }
                    }
                    OsmMapView(
                        initialLat = mapCenterLat,
                        initialLng = mapCenterLng,
                        initialZoom = 14.0,
                        pins = previewPins,
                        isInteractive = false,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Overlay clickeable para garantizar tap sobre AndroidView
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(onClick = onOpenMap)
                    )
                    Surface(
                        color = PureWhite.copy(alpha = 0.94f),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.explore_subtitle),
                                style = MaterialTheme.typography.labelMedium,
                                color = TextoPrincipal
                            )
                            Button(
                                onClick = onOpenMap,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CacaoDark,
                                    contentColor = PureWhite
                                ),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.btn_open_map),
                                    color = PureWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Sección Populares
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.section_popular),
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = onSeeAllPopular) {
                    Text(
                        text = stringResource(R.string.action_see_all),
                        color = CacaoDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = CacaoDark,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // Grid 2 columnas con datos reales de la API
        when {
            uiState.isLoading -> {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp)
                    ) {
                        CircularProgressIndicator(color = CacaoDark)
                    }
                }
            }
            uiState.errorMessage != null -> {
                item {
                    ErrorStateBox(
                        message = uiState.errorMessage!!,
                        onRetry = viewModel::loadHomeData
                    )
                }
            }
            uiState.popularEstablishments.isEmpty() -> {
                item {
                    EmptyStateBox(
                        icon = Icons.Rounded.Storefront,
                        title = stringResource(R.string.empty_popular)
                    )
                }
            }
            else -> {
                val rows = uiState.popularEstablishments.take(4).chunked(2)
                items(rows) { rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                    ) {
                        rowItems.forEach { est ->
                            BusinessGridCard(
                                establishment = est,
                                onCardClick = { onOpenDetail(est.id) },
                                onFavoriteClick = {
                                    if (viewModel.isAuthenticated) {
                                        viewModel.toggleFavorite(est.id, onShowMessage)
                                    } else {
                                        onRequireAuthForFavorite(est)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
