package com.abad.campusgps.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val EsquemaClaro = lightColorScheme(
    primary = Petroleo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC6ECE3),
    onPrimaryContainer = Color(0xFF00443D),
    secondary = Turquesa,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD0F1EF),
    onSecondaryContainer = Color(0xFF004B4D),
    background = FondoClaro,
    onBackground = Color(0xFF18332F),
    surface = Color(0xFFFCFEFD),
    onSurface = Color(0xFF18332F),
    surfaceVariant = Color(0xFFDDEEE9),
    onSurfaceVariant = Color(0xFF405B54),
    outline = Color(0xFF627F77),
    outlineVariant = Color(0xFFBDCFC8)
)

private val EsquemaOscuro = darkColorScheme(
    primary = TurquesaClaro,
    onPrimary = Color(0xFF003731),
    primaryContainer = Color(0xFF154941),
    onPrimaryContainer = Color(0xFFBAF0E5),
    secondary = TurquesaClaro,
    onSecondary = Color(0xFF003733),
    secondaryContainer = Color(0xFF183D3D),
    onSecondaryContainer = Color(0xFFAFE7E4),
    background = FondoOscuro,
    onBackground = Color(0xFFDCE5E0),
    surface = Color(0xFF192A26),
    onSurface = Color(0xFFDCE5E0),
    surfaceVariant = Color(0xFF263C36),
    onSurfaceVariant = Color(0xFFBBD3CA),
    outline = Color(0xFF8AA89D),
    outlineVariant = Color(0xFF405B53)
)

private val FormasCampus = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(26.dp)
)

@Composable
fun CampusGPSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography,
        shapes = FormasCampus,
        content = content
    )
}
