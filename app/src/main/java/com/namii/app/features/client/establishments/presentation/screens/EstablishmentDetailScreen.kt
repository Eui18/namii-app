package com.namii.app.features.client.establishments.presentation.screens

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.namii.app.R
import com.namii.app.core.ui.components.EmptyStateBox
import com.namii.app.core.ui.components.ErrorStateBox
import com.namii.app.core.ui.components.EstablishmentCoverVisual
import com.namii.app.core.ui.components.InlineStatusPill
import com.namii.app.core.ui.components.LoadingStateBox
import com.namii.app.core.ui.components.ProductMenuCard
import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.presentation.viewmodels.EstablishmentDetailViewModel
import com.namii.app.features.client.products.domain.entities.ProductCategory
import com.namii.app.ui.theme.BorderLight
import com.namii.app.ui.theme.CacaoDark
import com.namii.app.ui.theme.DuraznoClear
import com.namii.app.ui.theme.DuraznoLight
import com.namii.app.ui.theme.EstrellaAmber
import com.namii.app.ui.theme.PureWhite
import com.namii.app.ui.theme.RojoGuajillo
import com.namii.app.ui.theme.TextoMuted
import com.namii.app.ui.theme.TextoPrincipal
import com.namii.app.ui.theme.TextoSecundario
import java.util.Locale

/**
 * Vista 6. Perfil del negocio y menú completo (incluyendo productos agotados marcados como "Agotado")
 */
@Composable
fun EstablishmentDetailScreen(
    onBack: () -> Unit,
    onViewInMap: (lat: Double, lng: Double, establishmentId: Int) -> Unit,
    onRequireAuthForFavorite: (Establishment) -> Unit,
    onRequireAuthForRating: (Establishment, stars: Int) -> Unit,
    onShowMessage: (String) -> Unit,
    pendingRatingToSubmit: Int? = null,
    onConsumedPendingRating: () -> Unit = {},
    viewModel: EstablishmentDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(pendingRatingToSubmit, uiState.isAuthenticated) {
        if (pendingRatingToSubmit != null && uiState.isAuthenticated) {
            onConsumedPendingRating()
            viewModel.submitRating(
                stars = pendingRatingToSubmit,
                onSuccess = onShowMessage,
                onError = onShowMessage
            )
        }
    }

    when {
        uiState.isLoading -> LoadingStateBox(modifier = Modifier.background(PureWhite))
        uiState.errorMessage != null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PureWhite)
            ) {
                Row(modifier = Modifier.padding(8.dp)) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                            tint = TextoPrincipal
                        )
                    }
                }
                ErrorStateBox(
                    message = uiState.errorMessage!!,
                    onRetry = viewModel::loadDetail
                )
            }
        }
        uiState.establishment != null -> {
            val est = uiState.establishment!!

            LazyColumn(
                contentPadding = PaddingValues(bottom = 28.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .background(PureWhite)
            ) {
                // Hero Portada + botón atrás + corazón (vacío por defecto si no está guardado)
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        EstablishmentCoverVisual(
                            imageUrl = est.coverPhotoUrl,
                            fallbackColor = est.category.color,
                            contentDescription = est.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = PureWhite.copy(alpha = 0.92f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                IconButton(onClick = onBack, modifier = Modifier.fillMaxSize()) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = null,
                                        tint = TextoPrincipal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Surface(
                                shape = CircleShape,
                                color = PureWhite.copy(alpha = 0.92f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        if (uiState.isAuthenticated) {
                                            viewModel.toggleFavorite(onShowMessage)
                                        } else {
                                            onRequireAuthForFavorite(est)
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = if (est.isFavorite) {
                                            Icons.Rounded.Favorite
                                        } else {
                                            Icons.Rounded.FavoriteBorder
                                        },
                                        contentDescription = null,
                                        tint = if (est.isFavorite) RojoGuajillo else TextoPrincipal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Círculo superpuesto con el icono de categoría
                        Surface(
                            shape = CircleShape,
                            color = est.category.color,
                            border = BorderStroke(3.dp, PureWhite),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 20.dp)
                                .offset(y = 22.dp)
                                .size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = est.category.icon,
                                    contentDescription = est.category.displayName,
                                    tint = PureWhite,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                // Cabecera información general del negocio
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 30.dp)
                    ) {
                        Text(
                            text = est.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextoPrincipal
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = est.category.color,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = est.category.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            InlineStatusPill(isOpen = est.isOpen)
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = EstrellaAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format(
                                    Locale.US,
                                    "%.1f (%d calificaciones)",
                                    est.averageRating,
                                    est.ratingCount
                                ),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoPrincipal
                            )
                        }

                        if (!est.description.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = est.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoSecundario
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Bloque durazno con dirección, "Ver en el mapa" y horarios
                        Surface(
                            color = DuraznoClear,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.LocationOn,
                                            contentDescription = null,
                                            tint = CacaoDark,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = est.address.ifBlank { "Ubicación registrada en el mapa" },
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextoPrincipal
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${stringResource(R.string.action_view_on_map)} →",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CacaoDark,
                                        modifier = Modifier.clickable {
                                            onViewInMap(est.latitude, est.longitude, est.id)
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Rounded.Schedule,
                                        contentDescription = null,
                                        tint = TextoSecundario,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = est.scheduleSummary(),
                                        fontSize = 11.sp,
                                        color = TextoSecundario
                                    )
                                }
                            }
                        }
                    }
                }

                // Título Menú y chips de filtro por categoría de producto (Todos, Bebidas, Alimentos, Postres, Antojitos)
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = stringResource(R.string.section_menu),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            MenuCategoryChip(
                                label = stringResource(R.string.menu_chip_all),
                                selected = uiState.selectedCategory == null,
                                onClick = { viewModel.selectProductCategory(null) }
                            )
                        }
                        items(ProductCategory.entries, key = { it.name }) { cat ->
                            MenuCategoryChip(
                                label = cat.displayName,
                                selected = uiState.selectedCategory == cat,
                                onClick = { viewModel.selectProductCategory(cat) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Grid en 2 columnas con productos (disponibles y agotados)
                val products = uiState.visibleProducts
                if (products.isEmpty()) {
                    item {
                        EmptyStateBox(
                            icon = Icons.Rounded.RestaurantMenu,
                            title = stringResource(R.string.menu_empty)
                        )
                    }
                } else {
                    items(products.chunked(2)) { rowProducts ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                        ) {
                            rowProducts.forEach { product ->
                                ProductMenuCard(
                                    product = product,
                                    accentColor = est.category.color,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowProducts.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Tarjeta "Califica este lugar" (1 a 5 estrellas, sin comentarios escritos)
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Surface(
                        color = DuraznoLight,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderLight),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.rate_place_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = TextoPrincipal
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val selectedStars = est.userRating ?: 0
                                for (starValue in 1..5) {
                                    val filled = starValue <= selectedStars
                                    IconButton(
                                        onClick = {
                                            if (uiState.isAuthenticated) {
                                                viewModel.submitRating(
                                                    stars = starValue,
                                                    onSuccess = onShowMessage,
                                                    onError = onShowMessage
                                                )
                                            } else {
                                                onRequireAuthForRating(est, starValue)
                                            }
                                        },
                                        enabled = !uiState.isRatingSubmitting,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (filled) {
                                                Icons.Rounded.Star
                                            } else {
                                                Icons.Rounded.StarBorder
                                            },
                                            contentDescription = "$starValue estrellas",
                                            tint = if (filled) EstrellaAmber else TextoMuted,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (uiState.isAuthenticated) {
                                    stringResource(R.string.rate_logged_hint)
                                } else {
                                    stringResource(R.string.rate_guest_hint)
                                },
                                fontSize = 11.sp,
                                color = TextoSecundario
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuCategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) CacaoDark else DuraznoClear,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) PureWhite else CacaoDark,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}
