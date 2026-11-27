package com.namii.app.features.client.establishments.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.namii.app.R
import com.namii.app.core.ui.components.BusinessHorizontalCard
import com.namii.app.core.ui.components.EmptyStateBox
import com.namii.app.core.ui.components.ErrorStateBox
import com.namii.app.core.ui.components.LoadingStateBox
import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.presentation.viewmodels.PopularEstablishmentsViewModel
import com.namii.app.ui.theme.PureWhite
import com.namii.app.ui.theme.TextoPrincipal
import com.namii.app.ui.theme.TextoSecundario

/**
 * Vista 3. Populares (Top 10 sin filtros ni promociones)
 */
@Composable
fun PopularEstablishmentsScreen(
    onBack: () -> Unit,
    onOpenDetail: (Int) -> Unit,
    onRequireAuthForFavorite: (Establishment) -> Unit,
    onShowMessage: (String) -> Unit,
    viewModel: PopularEstablishmentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
    ) {
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
                text = stringResource(R.string.title_popular),
                style = MaterialTheme.typography.titleLarge,
                color = TextoPrincipal
            )
        }

        Text(
            text = stringResource(R.string.popular_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {
            uiState.isLoading -> LoadingStateBox()
            uiState.errorMessage != null -> {
                ErrorStateBox(
                    message = uiState.errorMessage!!,
                    onRetry = viewModel::loadPopular
                )
            }
            uiState.establishments.isEmpty() -> {
                EmptyStateBox(
                    icon = Icons.Rounded.StarOutline,
                    title = stringResource(R.string.empty_popular)
                )
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(
                        items = uiState.establishments,
                        key = { _, est -> est.id }
                    ) { index, est ->
                        BusinessHorizontalCard(
                            establishment = est,
                            rankNumber = index + 1,
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
