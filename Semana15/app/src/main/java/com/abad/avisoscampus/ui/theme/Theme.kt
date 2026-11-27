package com.abad.avisoscampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta fija: no se usan colores dinámicos para conservar la identidad de la app
private val EsquemaAvisos = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = Color.White,
    primaryContainer = CelesteSuave,
    onPrimaryContainer = AzulInstitucional,
    secondary = AzulSecundario,
    onSecondary = Color.White,
    secondaryContainer = CelesteSuave,
    onSecondaryContainer = AzulInstitucional,
    tertiary = Turquesa,
    onTertiary = Color.White,
    background = Fondo,
    onBackground = TextoPrincipal,
    surface = Superficie,
    onSurface = TextoPrincipal,
    surfaceVariant = GrisSuave,
    onSurfaceVariant = TextoSecundario,
    surfaceContainerLowest = Superficie,
    surfaceContainerLow = Superficie,
    surfaceContainer = Superficie,
    surfaceContainerHigh = Fondo,
    surfaceContainerHighest = GrisSuave,
    outline = Bordes,
    outlineVariant = Bordes,
    error = CoralError,
    onError = Color.White,
    errorContainer = CoralSuave,
    onErrorContainer = Color(0xFF8E2A35)
)

@Composable
fun AvisosCampusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaAvisos,
        typography = Typography,
        content = content
    )
}
