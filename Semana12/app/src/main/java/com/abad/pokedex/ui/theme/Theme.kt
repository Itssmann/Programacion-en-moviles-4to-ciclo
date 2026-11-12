package com.abad.pokedex.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFE5484D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDADB),
    onPrimaryContainer = Color(0xFF67191D),

    secondary = Color(0xFF5266A0),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDDE5FF),
    onSecondaryContainer = Color(0xFF243763),

    tertiary = Color(0xFFE5AD35),
    onTertiary = Color(0xFF332400),

    background = Color(0xFFF6F7FB),
    onBackground = Color(0xFF25283A),

    surface = Color.White,
    onSurface = Color(0xFF25283A),
    surfaceVariant = Color(0xFFEEF0F6),
    onSurfaceVariant = Color(0xFF606779),

    outline = Color(0xFFB8BECA)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB4B6),
    onPrimary = Color(0xFF690D18),
    primaryContainer = Color(0xFF8F1E2A),
    onPrimaryContainer = Color(0xFFFFDADB),

    secondary = Color(0xFFB7C5FF),
    onSecondary = Color(0xFF1B2A52),
    secondaryContainer = Color(0xFF394A7C),
    onSecondaryContainer = Color(0xFFDDE5FF),

    tertiary = Color(0xFFFFD17A),
    onTertiary = Color(0xFF3E2D00),

    background = Color(0xFF141720),
    onBackground = Color(0xFFF0F1F7),

    surface = Color(0xFF1C202B),
    onSurface = Color(0xFFF0F1F7),
    surfaceVariant = Color(0xFF303644),
    onSurfaceVariant = Color(0xFFC1C6D2),

    outline = Color(0xFF8B92A3)
)

private val PokedexShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun PokedexAbadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) {
            DarkColorScheme
        } else {
            LightColorScheme
        },
        typography = Typography,
        shapes = PokedexShapes,
        content = content
    )
}
