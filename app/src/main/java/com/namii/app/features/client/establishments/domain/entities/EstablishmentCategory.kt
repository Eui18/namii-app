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
        apiKey = "RESTAURANTES",
        displayName = "Restaurantes",
        color = CatRestaurantes,
        icon = Icons.Rounded.Restaurant
    ),
    CAFETERIAS(
        apiKey = "CAFETERIAS",
        displayName = "Cafeterías",
        color = CatCafeterias,
        icon = Icons.Rounded.Coffee
    ),
    TAQUERIAS(
        apiKey = "TAQUERIAS",
        displayName = "Taquerías",
        color = CatTaquerias,
        icon = Icons.Rounded.LocalDining
    ),
    PANADERIAS(
        apiKey = "PANADERIAS",
        displayName = "Panaderías",
        color = CatPanaderias,
        icon = Icons.Rounded.BakeryDining
    ),
    REPOSTERIAS(
        apiKey = "REPOSTERIAS",
        displayName = "Reposterías",
        color = CatReposterias,
        icon = Icons.Rounded.Cookie
    ),
    PIZZERIAS(
        apiKey = "PIZZERIAS",
        displayName = "Pizzerías",
        color = CatPizzerias,
        icon = Icons.Rounded.LocalPizza
    ),
    FONDAS(
        apiKey = "FONDAS",
        displayName = "Fondas",
        color = CatFondas,
        icon = Icons.Rounded.SoupKitchen
    ),
    COMIDA_RAPIDA(
        apiKey = "COMIDA_RAPIDA",
        displayName = "Comida rápida",
        color = CatComidaRapida,
        icon = Icons.Rounded.Fastfood
    ),
    BEBIDAS(
        apiKey = "BEBIDAS",
        displayName = "Bebidas",
        color = CatBebidas,
        icon = Icons.Rounded.LocalBar
    ),
    POSTRES(
        apiKey = "POSTRES",
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
