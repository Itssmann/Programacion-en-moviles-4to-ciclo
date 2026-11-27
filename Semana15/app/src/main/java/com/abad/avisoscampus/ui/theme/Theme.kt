package com.abad.avisoscampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AzulSecundario,
    secondary = AzulClaro
)

private val LightColorScheme = lightColorScheme(
    primary = AzulCampus,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    secondary = AzulSecundario,
    background = FondoGeneral,
    surface = ColorTarjeta
)

@Composable
fun AvisosCampusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
