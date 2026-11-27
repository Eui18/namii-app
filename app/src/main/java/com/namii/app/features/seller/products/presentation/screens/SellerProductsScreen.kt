package com.namii.app.features.seller.products.presentation.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.namii.app.core.ui.components.EmptyStateBox
import com.namii.app.core.ui.components.ErrorStateBox
import com.namii.app.core.ui.components.LoadingStateBox
import com.namii.app.features.client.products.domain.entities.Product
import com.namii.app.features.client.products.domain.entities.ProductCategory
import com.namii.app.features.seller.products.presentation.viewmodels.SellerProductsViewModel
import com.namii.app.ui.theme.BorderLight
import com.namii.app.ui.theme.DuraznoClear
import com.namii.app.ui.theme.DuraznoLight
import com.namii.app.ui.theme.ErrorRed
import com.namii.app.ui.theme.ErrorSurface
import com.namii.app.ui.theme.ExitoGreen
import com.namii.app.ui.theme.ExitoSurface
import com.namii.app.ui.theme.NaranjaTostado
import com.namii.app.ui.theme.PureWhite
import com.namii.app.ui.theme.SellerCacaoInk
import com.namii.app.ui.theme.SellerSoftBrown
import com.namii.app.ui.theme.TextoMuted
import java.util.Locale

/**
 * V-07: Gestión del menú y productos (con filtros Todos / Bebidas / Alimentos / Postres / Antojitos)
 * + V-09: Diálogo de confirmación de eliminación.
 */
@Composable
fun SellerProductsScreen(
    onBack: () -> Unit,
    onAddProduct: (establishmentId: Int) -> Unit,
    onEditProduct: (establishmentId: Int, productId: Int) -> Unit,
    onShowMessage: (String) -> Unit,
    viewModel: SellerProductsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    Scaffold(
        containerColor = PureWhite,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddProduct(state.establishmentId) },
                containerColor = NaranjaTostado,
                contentColor = PureWhite
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Agregar producto",
                    tint = PureWhite
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mi menú",
                        style = MaterialTheme.typography.titleLarge,
                        color = SellerCacaoInk
                    )
                    Text(
                        text = "Administra platillos, precios y disponibilidad",
                        fontSize = 12.sp,
                        color = SellerSoftBrown
                    )
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                item {
                    CategoryFilterPill(
                        label = "Todos",
                        selected = state.selectedCategory == null,
                        onClick = { viewModel.selectCategory(null) }
                    )
                }
                items(ProductCategory.entries, key = { it.name }) { category ->
                    CategoryFilterPill(
                        label = category.displayName,
                        selected = state.selectedCategory == category,
                        onClick = { viewModel.selectCategory(category) }
                    )
                }
            }

            when {
                state.isLoading -> LoadingStateBox()
                state.errorMessage != null -> ErrorStateBox(
                    message = state.errorMessage!!,
                    onRetry = viewModel::loadProducts
                )
                state.visibleProducts.isEmpty() -> {
                    EmptyStateBox(
                        icon = Icons.Rounded.RestaurantMenu,
                        title = if (state.selectedCategory == null) {
                            "Tu menú aún está vacío"
                        } else {
                            "Sin productos en ${state.selectedCategory?.displayName}"
                        },
                        subtitle = "Toca el botón + para registrar tus platillos, bebidas o postres.",
                        iconTint = NaranjaTostado,
                        containerColor = DuraznoClear
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 92.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.visibleProducts, key = { it.id }) { product ->
                            SellerProductCard(
                                product = product,
                                onToggleAvailability = {
                                    viewModel.toggleAvailability(product, onShowMessage)
                                },
                                onEdit = { onEditProduct(state.establishmentId, product.id) },
                                onDelete = { viewModel.requestDelete(product) }
                            )
                        }
                    }
                }
            }
        }
    }

    state.pendingDeleteProduct?.let { productToDelete ->
        DeleteProductConfirmDialog(
            productName = productToDelete.name,
            isDeleting = state.isDeleting,
            onConfirm = { viewModel.confirmDelete(onShowMessage) },
            onDismiss = viewModel::dismissDeleteDialog
        )
    }
}

@Composable
private fun CategoryFilterPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (selected) NaranjaTostado else DuraznoLight,
        border = BorderStroke(1.dp, if (selected) NaranjaTostado else BorderLight),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            color = if (selected) PureWhite else SellerCacaoInk,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun SellerProductCard(
    product: Product,
    onToggleAvailability: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(DuraznoClear)
                ) {
                    if (product.photoUrl.isNotBlank()) {
                        AsyncImage(
                            model = product.photoUrl,
                            contentDescription = product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Fastfood,
                            contentDescription = null,
                            tint = NaranjaTostado,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = product.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SellerCacaoInk,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            color = if (product.isAvailable) ExitoSurface else ErrorSurface,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (product.isAvailable) "Disponible" else "Agotado",
                                color = if (product.isAvailable) ExitoGreen else ErrorRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${product.category.displayName} · \$${String.format(Locale.US, "%.2f", product.price)} MXN",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NaranjaTostado
                    )

                    if (!product.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = product.description,
                            fontSize = 11.sp,
                            color = SellerSoftBrown,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = product.isAvailable,
                        onCheckedChange = { onToggleAvailability() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PureWhite,
                            checkedTrackColor = ExitoGreen,
                            uncheckedThumbColor = PureWhite,
                            uncheckedTrackColor = TextoMuted
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (product.isAvailable) "En existencia" else "Agotado",
                        fontSize = 12.sp,
                        color = SellerSoftBrown
                    )
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Editar",
                            tint = NaranjaTostado
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteOutline,
                            contentDescription = "Eliminar",
                            tint = ErrorRed
                        )
                    }
                }
            }
        }
    }
}

/**
 * V-09: Confirmación de eliminación de producto.
 */
@Composable
fun DeleteProductConfirmDialog(
    productName: String,
    isDeleting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        title = {
            Text(
                text = "Eliminar producto",
                style = MaterialTheme.typography.titleMedium,
                color = SellerCacaoInk
            )
        },
        text = {
            Text(
                text = "¿Seguro que deseas eliminar \"$productName\" de tu menú? Esta acción no se puede deshacer.",
                fontSize = 13.sp,
                color = SellerSoftBrown
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isDeleting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorRed,
                    contentColor = PureWhite
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isDeleting) "Eliminando..." else "Sí, eliminar",
                    color = PureWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isDeleting) {
                Text(
                    text = "Cancelar",
                    color = SellerSoftBrown,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}
