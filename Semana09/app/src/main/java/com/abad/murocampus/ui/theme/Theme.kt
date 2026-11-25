package com.abad.murocampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AzulCampus,
    onPrimary = Color.White,
    primaryContainer = AzulContenedor,
    onPrimaryContainer = Color(0xFF00184A),
    secondary = Coral,
    onSecondary = Color.White,
    background = FondoClaro,
    surface = Color.White,
    onBackground = TextoClaro,
    onSurface = TextoClaro
)

private val DarkColorScheme = darkColorScheme(
    primary = AzulClaro,
    onPrimary = Color(0xFF002A75),
    primaryContainer = AzulCampus,
    onPrimaryContainer = AzulContenedor,
    secondary = CoralClaro,
    onSecondary = Color(0xFF5F1607),
    background = FondoOscuro,
    surface = SuperficieOscura,
    onBackground = TextoOscuro,
    onSurface = TextoOscuro
)

@Composable
fun MuroCampusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
