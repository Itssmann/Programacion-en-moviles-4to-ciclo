package com.abad.centrotareas.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF9CD5CA), onPrimary = Color(0xFF003833),
    primaryContainer = Color(0xFF164E48), onPrimaryContainer = Color(0xFFB9EFE4),
    secondary = Color(0xFF80D5CB), onSecondary = Color(0xFF003733),
    secondaryContainer = Color(0xFF244C47), onSecondaryContainer = Color(0xFFB5EDE2),
    background = Color(0xFF101815), onBackground = Color(0xFFE0E9E3),
    surface = Color(0xFF17211D), onSurface = Color(0xFFE0E9E3),
    surfaceVariant = Color(0xFF2B3B35), onSurfaceVariant = Color(0xFFBECDC5),
    surfaceContainerLow = Color(0xFF1B2621), surfaceContainer = Color(0xFF223029),
    outline = Color(0xFF85988E), outlineVariant = Color(0xFF3C4E44),
    error = Color(0xFFFFB4AB), onError = Color(0xFF561B17),
    errorContainer = Color(0xFF72302A), onErrorContainer = Color(0xFFFFDAD6)
)
private val LightColorScheme = lightColorScheme(
    primary = VerdePetroleo, onPrimary = Color.White,
    primaryContainer = TurquesaAcento, onPrimaryContainer = Color(0xFF083D36),
    secondary = VerdeSecundario, onSecondary = Color.White,
    secondaryContainer = Color(0xFFD5ECE6), onSecondaryContainer = Color(0xFF204E45),
    background = FondoGeneral, onBackground = Color(0xFF1D3029),
    surface = ColorTarjeta, onSurface = Color(0xFF1D3029),
    surfaceVariant = Color(0xFFE5EDE7), onSurfaceVariant = Color(0xFF4E6258),
    surfaceContainerLow = Color(0xFFF0F5EF), surfaceContainer = Color(0xFFE9F0E9),
    outline = Color(0xFF71867B), outlineVariant = ColorBorde,
    error = RojoErrorTexto, onError = Color.White,
    errorContainer = RojoErrorFondo, onErrorContainer = Color(0xFF651F1A)
)

@Composable
fun CentroTareasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes(
            extraSmall = RoundedCornerShape(8.dp), small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(16.dp), large = RoundedCornerShape(24.dp),
            extraLarge = RoundedCornerShape(28.dp)
        ),
        content = content
    )
}
