package com.abad.murocampus.ui.theme

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
    primary = AzulCampus,
    onPrimary = Color.White,
    primaryContainer = AzulContenedor,
    onPrimaryContainer = AzulProfundo,
    secondary = Coral,
    onSecondary = Color.White,
    secondaryContainer = CoralContenedor,
    onSecondaryContainer = CoralProfundo,
    tertiary = Turquesa,
    onTertiary = Color.White,
    tertiaryContainer = TurquesaContenedor,
    onTertiaryContainer = Color(0xFF002022),
    background = FondoClaro,
    onBackground = TextoClaro,
    surface = FondoClaro,
    onSurface = TextoClaro,
    surfaceVariant = Color(0xFFDFE3EE),
    onSurfaceVariant = Color(0xFF434752),
    outline = Color(0xFF737784),
    outlineVariant = Color(0xFFC3C6D3),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF0F3FA),
    surfaceContainer = Color(0xFFEAEEF6),
    surfaceContainerHigh = Color(0xFFE4E8F1),
    surfaceContainerHighest = Color(0xFFDEE2EB),
    surfaceBright = FondoClaro,
    surfaceDim = Color(0xFFD6DAE3)
)

private val DarkColorScheme = darkColorScheme(
    primary = AzulClaro,
    onPrimary = Color(0xFF002C70),
    primaryContainer = AzulNoche,
    onPrimaryContainer = AzulContenedor,
    secondary = CoralClaro,
    onSecondary = Color(0xFF5F1607),
    secondaryContainer = CoralNoche,
    onSecondaryContainer = CoralContenedor,
    tertiary = TurquesaClaro,
    onTertiary = Color(0xFF00373A),
    tertiaryContainer = TurquesaNoche,
    onTertiaryContainer = TurquesaContenedor,
    background = FondoOscuro,
    onBackground = TextoOscuro,
    surface = FondoOscuro,
    onSurface = TextoOscuro,
    surfaceVariant = Color(0xFF434752),
    onSurfaceVariant = Color(0xFFC3C6D3),
    outline = Color(0xFF8D909D),
    outlineVariant = Color(0xFF434752),
    surfaceContainerLowest = Color(0xFF1A1E27),
    surfaceContainerLow = Color(0xFF1F232C),
    surfaceContainer = Color(0xFF232731),
    surfaceContainerHigh = Color(0xFF2B2F39),
    surfaceContainerHighest = Color(0xFF363A44),
    surfaceBright = Color(0xFF363942),
    surfaceDim = FondoOscuro
)

private val Formas = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp)
)

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
