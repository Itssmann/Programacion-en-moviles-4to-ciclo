package com.abad.lab05navegacion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurpleLight,
    onPrimary = Color.White,
    primaryContainer = PrimaryPurpleDark,
    onPrimaryContainer = Color.White,
    secondary = PrimaryPurple,
    onSecondary = Color.White,
    background = Color(0xFF1C1A22),
    surface = Color(0xFF27242E),
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFCCC5D8)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = PurpleSurface,
    onPrimaryContainer = PrimaryPurpleDark,
    secondary = PrimaryPurpleLight,
    onSecondary = Color.White,
    background = Color(0xFFF8F6FB),
    surface = SurfaceWhite,
    onSurface = TextDark,
    onSurfaceVariant = TextGray
)

@Composable
fun Lab05NavegacionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
