package com.abad.saludpluscitas.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AzulSaludSecondary,
    secondary = AzulSaludPrimaryContainer,
    tertiary = AzulSaludPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = AzulSaludPrimary,
    onPrimary = AzulSaludOnPrimary,
    primaryContainer = AzulSaludPrimaryContainer,
    onPrimaryContainer = AzulSaludOnPrimaryContainer,
    secondary = AzulSaludSecondary,
    onSecondary = AzulSaludOnSecondary,
    secondaryContainer = AzulSaludSecondaryContainer,
    background = FondoSalud,
    onBackground = TextoPrincipal,
    surface = SuperficieSalud,
    onSurface = TextoPrincipal,
    surfaceVariant = SuperficieSalud,
    onSurfaceVariant = TextoSecundario,
    outline = BordeCampo
)

@Composable
fun SaludPlusCitasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
