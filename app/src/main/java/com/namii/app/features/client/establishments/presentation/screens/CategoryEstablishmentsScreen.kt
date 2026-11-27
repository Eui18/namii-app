package com.namii.app.features.client.establishments.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.namii.app.core.ui.components.BusinessHorizontalCard
import com.namii.app.core.ui.components.EmptyStateBox
import com.namii.app.core.ui.components.ErrorStateBox
import com.namii.app.core.ui.components.LoadingStateBox
import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.domain.entities.EstablishmentCategory
import com.namii.app.features.client.establishments.presentation.viewmodels.CategoryEstablishmentsViewModel
import com.namii.app.ui.theme.CacaoDark
import com.namii.app.ui.theme.DuraznoClear
import com.namii.app.ui.theme.PureWhite
import com.namii.app.ui.theme.TextoMuted
import com.namii.app.ui.theme.TextoPrincipal
import com.namii.app.ui.theme.TextoSecundario

/**
 * Vista 2. Lista de negocios por categoría
 */
@Composable
fun CategoryEstablishmentsScreen(
    onBack: () -> Unit,
    onOpenDetail: (Int) -> Unit,
    onRequireAuthForFavorite: (Establishment) -> Unit,
    onShowMessage: (String) -> Unit,
    viewModel: CategoryEstablishmentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val chipsListState = rememberLazyListState()

    LaunchedEffect(uiState.selectedCategory) {
        val index = uiState.selectedCategory?.let { it.ordinal + 1 } ?: 0
        chipsListState.animateScrollToItem(index)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
    ) {
        // Barra superior
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = TextoPrincipal
                )
            }
            Text(
                text = stringResource(R.string.title_establishments),
                style = MaterialTheme.typography.titleLarge,
                color = TextoPrincipal
            )
        }

        // Fila fija de chips: "Todo" + 10 categorías
        LazyRow(
            state = chipsListState,
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                val isAllSelected = uiState.selectedCategory == null
                CategoryFilterChip(
                    label = stringResource(R.string.chip_all),
                    selected = isAllSelected,
                    selectedColor = CacaoDark,
                    onClick = { viewModel.selectCategory(null) }
                )
            }
            items(EstablishmentCategory.entries, key = { it.name }) { category ->
                CategoryFilterChip(
                    label = category.displayName,
                    selected = uiState.selectedCategory == category,
                    selectedColor = category.color,
                    onClick = { viewModel.selectCategory(category) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when {
            uiState.isLoading -> LoadingStateBox()
            uiState.errorMessage != null -> {
                ErrorStateBox(
                    message = uiState.errorMessage!!,
                    onRetry = viewModel::retry
                )
            }
            uiState.establishments.isEmpty() -> {
                val icon = uiState.selectedCategory?.icon ?: Icons.Rounded.Storefront
                EmptyStateBox(
                    icon = icon,
                    iconTint = TextoMuted,
                    title = stringResource(R.string.empty_category)
                )
            }
            else -> {
                val count = uiState.establishments.size
                val countLabel = if (count == 1) "1 negocio" else "$count negocios"
                Text(
                    text = countLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.establishments, key = { it.id }) { est ->
                        BusinessHorizontalCard(
                            establishment = est,
                            onCardClick = { onOpenDetail(est.id) },
                            onFavoriteClick = {
                                if (viewModel.isAuthenticated) {
                                    viewModel.toggleFavorite(est.id, onShowMessage)
                                } else {
                                    onRequireAuthForFavorite(est)
                                }
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterChip(
    label: String,
    selected: Boolean,
    selectedColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (selected) selectedColor else DuraznoClear,
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) PureWhite else CacaoDark,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
