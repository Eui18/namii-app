package com.namii.app.features.client.favorites.presentation.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.namii.app.R
import com.namii.app.core.ui.components.BusinessHorizontalCard
import com.namii.app.core.ui.components.EmptyStateBox
import com.namii.app.core.ui.components.ErrorStateBox
import com.namii.app.core.ui.components.LoadingStateBox
import com.namii.app.features.client.favorites.presentation.viewmodels.FavoritesViewModel
import com.namii.app.ui.theme.BorderLight
import com.namii.app.ui.theme.CacaoDark
import com.namii.app.ui.theme.DuraznoClear
import com.namii.app.ui.theme.ErrorRed
import com.namii.app.ui.theme.PureWhite
import com.namii.app.ui.theme.RojoGuajillo
import com.namii.app.ui.theme.TextoMuted
import com.namii.app.ui.theme.TextoPrincipal
import com.namii.app.ui.theme.TextoSecundario

/**
 * Vista 7. Favoritos (estado sin sesión y estado con sesión)
 */
@Composable
fun FavoritesScreen(
    onOpenDetail: (Int) -> Unit,
    onOpenLogin: () -> Unit,
    onOpenRegister: () -> Unit,
    onShowMessage: (String) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.user == null) {
        // Estado sin sesión
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PureWhite)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.nav_favorites),
                style = MaterialTheme.typography.headlineMedium,
                color = TextoPrincipal
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Surface(
                    color = DuraznoClear,
                    shape = CircleShape,
                    modifier = Modifier.size(84.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Favorite,
                            contentDescription = null,
                            tint = RojoGuajillo,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.favorites_guest_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipal,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.favorites_guest_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(26.dp))

                Button(
                    onClick = onOpenLogin,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CacaoDark,
                        contentColor = PureWhite
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = stringResource(R.string.btn_login),
                        color = PureWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onOpenRegister,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DuraznoClear,
                        contentColor = CacaoDark
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = stringResource(R.string.btn_create_account),
                        color = CacaoDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    } else {
        // Estado con sesión
        var menuExpanded by remember { mutableStateOf(false) }

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
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.title_my_favorites),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextoPrincipal
                )

                Box {
                    Surface(
                        color = DuraznoClear,
                        shape = CircleShape,
                        modifier = Modifier.size(38.dp)
                    ) {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = null,
                                tint = CacaoDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        containerColor = PureWhite
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                            Text(
                                text = uiState.user!!.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextoPrincipal
                            )
                            Text(
                                text = uiState.user!!.email,
                                fontSize = 12.sp,
                                color = TextoSecundario
                            )
                        }
                        HorizontalDivider(color = BorderLight)
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.btn_logout),
                                    color = ErrorRed,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                viewModel.logout()
                            }
                        )
                    }
                }
            }

            when {
                uiState.isLoading -> LoadingStateBox()
                uiState.errorMessage != null -> {
                    ErrorStateBox(
                        message = uiState.errorMessage!!,
                        onRetry = viewModel::loadFavorites
                    )
                }
                uiState.favorites.isEmpty() -> {
                    EmptyStateBox(
                        icon = Icons.Rounded.FavoriteBorder,
                        iconTint = TextoMuted,
                        title = stringResource(R.string.favorites_empty)
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.favorites, key = { it.id }) { est ->
                            BusinessHorizontalCard(
                                establishment = est,
                                onCardClick = { onOpenDetail(est.id) },
                                onFavoriteClick = {
                                    viewModel.removeFavorite(est.id, onShowMessage)
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}
