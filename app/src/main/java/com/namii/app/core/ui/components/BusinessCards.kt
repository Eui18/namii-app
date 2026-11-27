package com.namii.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.namii.app.R
import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.products.domain.entities.Product
import com.namii.app.ui.theme.BorderLight
import com.namii.app.ui.theme.CacaoDark
import com.namii.app.ui.theme.DuraznoClear
import com.namii.app.ui.theme.DuraznoLight
import com.namii.app.ui.theme.ErrorRed
import com.namii.app.ui.theme.ErrorSurface
import com.namii.app.ui.theme.EstrellaAmber
import com.namii.app.ui.theme.PureWhite
import com.namii.app.ui.theme.RojoGuajillo
import com.namii.app.ui.theme.TextoMuted
import com.namii.app.ui.theme.TextoPrincipal
import com.namii.app.ui.theme.TextoSecundario
import java.util.Locale

@Composable
fun EstablishmentCoverVisual(
    imageUrl: String?,
    fallbackColor: androidx.compose.ui.graphics.Color,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(fallbackColor.copy(alpha = 0.78f), fallbackColor.copy(alpha = 0.95f))
            )
        )
    ) {
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun BusinessGridCard(
    establishment: Establishment,
    onCardClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, BorderLight),
        modifier = modifier.clickable(onClick = onCardClick)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(106.dp)
            ) {
                EstablishmentCoverVisual(
                    imageUrl = establishment.coverPhotoUrl,
                    fallbackColor = establishment.category.color,
                    contentDescription = establishment.name,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    shape = CircleShape,
                    color = PureWhite.copy(alpha = 0.92f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(30.dp)
                ) {
                    IconButton(onClick = onFavoriteClick, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = if (establishment.isFavorite) {
                                Icons.Rounded.Favorite
                            } else {
                                Icons.Rounded.FavoriteBorder
                            },
                            contentDescription = null,
                            tint = if (establishment.isFavorite) RojoGuajillo else TextoMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = establishment.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(establishment.category.color)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = establishment.category.displayName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
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
                                "%.1f (%d)",
                                establishment.averageRating,
                                establishment.ratingCount
                            ),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BusinessHorizontalCard(
    establishment: Establishment,
    onCardClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    rankNumber: Int? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, BorderLight),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            if (rankNumber != null) {
                Surface(
                    shape = CircleShape,
                    color = if (rankNumber == 1) DuraznoClear else DuraznoLight,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(30.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "#$rankNumber",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (rankNumber == 1) CacaoDark else TextoSecundario
                        )
                    }
                }
            }

            EstablishmentCoverVisual(
                imageUrl = establishment.coverPhotoUrl,
                fallbackColor = establishment.category.color,
                contentDescription = establishment.name,
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(12.dp))
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
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.size(30.dp)
                    ) {
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
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(establishment.category.color)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = establishment.category.displayName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    InlineStatusPill(isOpen = establishment.isOpen)
                }

                Spacer(modifier = Modifier.height(6.dp))

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
    }
}

@Composable
fun SearchResultCard(
    establishment: Establishment,
    onCardClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, BorderLight),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                EstablishmentCoverVisual(
                    imageUrl = establishment.coverPhotoUrl,
                    fallbackColor = establishment.category.color,
                    contentDescription = establishment.name,
                    modifier = Modifier
                        .size(74.dp)
                        .clip(RoundedCornerShape(12.dp))
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
                        IconButton(onClick = onFavoriteClick, modifier = Modifier.size(30.dp)) {
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
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(establishment.category.color)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = establishment.category.displayName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        InlineStatusPill(isOpen = establishment.isOpen)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

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
                                "%.1f (%d)",
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

            if (establishment.matchedProducts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = establishment.category.softColor,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.search_matched_with),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextoSecundario
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        establishment.matchedProducts.forEach { product ->
                            Surface(
                                color = PureWhite,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, BorderLight),
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    if (!product.photoUrl.isNullOrBlank()) {
                                        AsyncImage(
                                            model = product.photoUrl,
                                            contentDescription = product.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(18.dp)
                                                .clip(CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Text(
                                        text = product.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextoPrincipal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductMenuCard(
    product: Product,
    accentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val isSoldOut = !product.isAvailable

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isSoldOut) InputCardMutedColor else PureWhite,
        border = BorderStroke(1.dp, BorderLight),
        modifier = modifier
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp)
            ) {
                EstablishmentCoverVisual(
                    imageUrl = product.photoUrl,
                    fallbackColor = accentColor,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (isSoldOut) 0.45f else 1f)
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSoldOut) ErrorSurface else PureWhite.copy(alpha = 0.92f),
                    border = if (isSoldOut) BorderStroke(0.8.dp, ErrorRed) else null,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = if (isSoldOut) {
                            stringResource(R.string.product_sold_out)
                        } else {
                            product.category.displayName
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSoldOut) ErrorRed else CacaoDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSoldOut) TextoSecundario else TextoPrincipal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!product.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = product.description,
                        fontSize = 10.sp,
                        color = TextoMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = String.format(Locale.US, "$%.2f", product.price),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSoldOut) TextoMuted else CacaoDark
                    )
                    if (isSoldOut) {
                        Text(
                            text = stringResource(R.string.product_unavailable),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ErrorRed
                        )
                    }
                }
            }
        }
    }
}

private val InputCardMutedColor = androidx.compose.ui.graphics.Color(0xFFFAF6F4)
