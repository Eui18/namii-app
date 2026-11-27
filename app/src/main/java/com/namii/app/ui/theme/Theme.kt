package com.namii.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val NamiiLightColorScheme = lightColorScheme(
    primary = CacaoDark,
    onPrimary = PureWhite,
    primaryContainer = DuraznoClear,
    onPrimaryContainer = CacaoDark,
    secondary = NaranjaTostado,
    onSecondary = PureWhite,
    secondaryContainer = DuraznoLight,
    onSecondaryContainer = TextoPrincipal,
    tertiary = RojoGuajillo,
    onTertiary = PureWhite,
    background = PureWhite,
    onBackground = TextoPrincipal,
    surface = PureWhite,
    onSurface = TextoPrincipal,
    surfaceVariant = DuraznoLight,
    onSurfaceVariant = TextoSecundario,
    outline = BorderLight,
    error = ErrorRed,
    onError = PureWhite
)

@Composable
fun ÑamiiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NamiiLightColorScheme,
        typography = Typography,
        content = content
    )
}
