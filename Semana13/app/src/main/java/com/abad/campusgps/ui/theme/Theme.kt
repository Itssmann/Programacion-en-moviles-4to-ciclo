package com.abad.campusgps.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TurquesaClaro,
    onPrimary = Color(0xFF003731),
    secondary = TurquesaClaro,
    background = FondoOscuro,
    surface = FondoOscuro,
    onBackground = Color(0xFFDCE5E0),
    onSurface = Color(0xFFDCE5E0)
)

private val LightColorScheme = lightColorScheme(
    primary = Petroleo,
    onPrimary = Color.White,
    secondary = Turquesa,
    background = FondoClaro,
    surface = FondoClaro,
    onBackground = TextoClaro,
    onSurface = TextoClaro
)

@Composable
fun CampusGPSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Paleta propia: no depende de los colores dinámicos del dispositivo.
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
