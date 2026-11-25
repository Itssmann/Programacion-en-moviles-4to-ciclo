package com.abad.murocampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = Esmeralda,
    onPrimary = Color.White,
    primaryContainer = EsmeraldaContenedor,
    onPrimaryContainer = EsmeraldaProfundo,
    secondary = Coral,
    onSecondary = Color.White,
    secondaryContainer = CoralContenedor,
    onSecondaryContainer = CoralProfundo,
    tertiary = Lima,
    onTertiary = Color.White,
    tertiaryContainer = LimaContenedor,
    onTertiaryContainer = Color(0xFF131F00),
    background = FondoClaro,
    onBackground = TextoClaro,
    surface = FondoClaro,
    onSurface = TextoClaro,
    surfaceVariant = Color(0xFFDBE5DF),
    onSurfaceVariant = Color(0xFF404944),
    outline = Color(0xFF707974),
    outlineVariant = Color(0xFFBFC9C3),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFEFF5F1),
    surfaceContainer = Color(0xFFE9EFEB),
    surfaceContainerHigh = Color(0xFFE3EAE5),
    surfaceContainerHighest = Color(0xFFDEE4DF),
    surfaceBright = FondoClaro,
    surfaceDim = Color(0xFFD5DBD7)
)

private val DarkColorScheme = darkColorScheme(
    primary = EsmeraldaClaro,
    onPrimary = Color(0xFF00382B),
    primaryContainer = EsmeraldaNoche,
    onPrimaryContainer = Color(0xFF9CF5D3),
    secondary = CoralClaro,
    onSecondary = Color(0xFF5F1609),
    secondaryContainer = CoralNoche,
    onSecondaryContainer = CoralContenedor,
    tertiary = LimaClaro,
    onTertiary = Color(0xFF243600),
    tertiaryContainer = LimaNoche,
    onTertiaryContainer = LimaContenedor,
    background = FondoOscuro,
    onBackground = TextoOscuro,
    surface = FondoOscuro,
    onSurface = TextoOscuro,
    surfaceVariant = Color(0xFF404944),
    onSurfaceVariant = Color(0xFFBFC9C3),
    outline = Color(0xFF89938E),
    outlineVariant = Color(0xFF404944),
    surfaceContainerLowest = Color(0xFF18211D),
    surfaceContainerLow = Color(0xFF1C2622),
    surfaceContainer = Color(0xFF202A26),
    surfaceContainerHigh = Color(0xFF2A3430),
    surfaceContainerHighest = Color(0xFF353F3A),
    surfaceBright = Color(0xFF343D39),
    surfaceDim = FondoOscuro
)

private val Formas = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp)
)

@Composable
private fun esOscuro(): Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f

@Composable
fun degradadoEncabezado(): Brush =
    Brush.linearGradient(if (esOscuro()) DegradadoOscuro else DegradadoClaro)

@Composable
fun degradadoBajoBarra(): Brush {
    val colores = if (esOscuro()) DegradadoOscuro else DegradadoClaro
    return Brush.verticalGradient(listOf(colores[0], colores[1]))
}

@Composable
fun colorEncabezado(): Color =
    if (esOscuro()) DegradadoOscuro.first() else DegradadoClaro.first()

@Composable
fun MuroCampusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = Formas,
        content = content
    )
}
