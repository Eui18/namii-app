package com.namii.app.features.client.establishments.domain.entities

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BakeryDining
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Cookie
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.LocalBar
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.LocalPizza
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.SoupKitchen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import com.namii.app.ui.theme.CatBebidas
import com.namii.app.ui.theme.CatCafeterias
import com.namii.app.ui.theme.CatComidaRapida
import com.namii.app.ui.theme.CatFondas
import com.namii.app.ui.theme.CatPanaderias
import com.namii.app.ui.theme.CatPizzerias
import com.namii.app.ui.theme.CatPostres
import com.namii.app.ui.theme.CatReposterias
import com.namii.app.ui.theme.CatRestaurantes
import com.namii.app.ui.theme.CatTaquerias

enum class EstablishmentCategory(
    val apiKey: String,
    val displayName: String,
    val color: Color,
    val icon: ImageVector
) {
    RESTAURANTES(
        apiKey = "Restaurantes",
        displayName = "Restaurantes",
        color = CatRestaurantes,
        icon = Icons.Rounded.Restaurant
    ),
    CAFETERIAS(
        apiKey = "Cafeterías",
        displayName = "Cafeterías",
        color = CatCafeterias,
        icon = Icons.Rounded.Coffee
    ),
    TAQUERIAS(
        apiKey = "Taquerías",
        displayName = "Taquerías",
        color = CatTaquerias,
        icon = Icons.Rounded.LocalDining
    ),
    PANADERIAS(
        apiKey = "Panaderías",
        displayName = "Panaderías",
        color = CatPanaderias,
        icon = Icons.Rounded.BakeryDining
    ),
    REPOSTERIAS(
        apiKey = "Reposterías",
        displayName = "Reposterías",
        color = CatReposterias,
        icon = Icons.Rounded.Cookie
    ),
    PIZZERIAS(
        apiKey = "Pizzerías",
        displayName = "Pizzerías",
        color = CatPizzerias,
        icon = Icons.Rounded.LocalPizza
    ),
    FONDAS(
        apiKey = "Fondas",
        displayName = "Fondas",
        color = CatFondas,
        icon = Icons.Rounded.SoupKitchen
    ),
    COMIDA_RAPIDA(
        apiKey = "Comida rápida",
        displayName = "Comida rápida",
        color = CatComidaRapida,
        icon = Icons.Rounded.Fastfood
    ),
    BEBIDAS(
        apiKey = "Bebidas",
        displayName = "Bebidas",
        color = CatBebidas,
        icon = Icons.Rounded.LocalBar
    ),
    POSTRES(
        apiKey = "Postres",
        displayName = "Postres",
        color = CatPostres,
        icon = Icons.Rounded.Cake
    );

    val colorInt: Int get() = color.toArgb()
    val softColor: Color get() = color.copy(alpha = 0.14f)

    companion object {
        fun fromRaw(raw: String?): EstablishmentCategory {
            if (raw.isNullOrBlank()) return RESTAURANTES
            val normalized = raw.trim().lowercase()
                .replace("í", "i")
                .replace("á", "a")
                .replace("é", "e")
                .replace("_", " ")
            return entries.firstOrNull { cat ->
                val catNorm = cat.displayName.lowercase()
                    .replace("í", "i")
                    .replace("á", "a")
                    .replace("é", "e")
                cat.name.equals(raw.trim(), ignoreCase = true) || catNorm == normalized
            } ?: RESTAURANTES
        }
    }
}
