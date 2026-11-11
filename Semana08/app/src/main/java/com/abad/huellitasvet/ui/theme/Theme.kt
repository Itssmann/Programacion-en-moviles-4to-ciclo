package com.abad.huellitasvet.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val ClientLightColorScheme = lightColorScheme(
    primary = ClientPrimary,
    onPrimary = ClientOnPrimary,
    primaryContainer = ClientPrimaryContainer,
    onPrimaryContainer = ClientOnPrimaryContainer,
    background = ClientBackground,
    surface = ClientSurface,
    onSurface = NeutralDark
)

private val VetLightColorScheme = lightColorScheme(
    primary = VetPrimary,
    onPrimary = VetOnPrimary,
    primaryContainer = VetPrimaryContainer,
    onPrimaryContainer = VetOnPrimaryContainer,
    background = VetBackground,
    surface = VetSurface,
    onSurface = NeutralDark
)

private val AdminLightColorScheme = lightColorScheme(
    primary = AdminPrimary,
    onPrimary = AdminOnPrimary,
    primaryContainer = AdminPrimaryContainer,
    onPrimaryContainer = AdminOnPrimaryContainer,
    background = AdminBackground,
    surface = AdminSurface,
    onSurface = NeutralDark
)

private val ClientDarkColorScheme = darkColorScheme(
    primary = ClientPrimary,
    onPrimary = ClientOnPrimary,
    primaryContainer = Color(0xFF7C2D12),
    onPrimaryContainer = Color(0xFFFFEDD5),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0)
)

private val VetDarkColorScheme = darkColorScheme(
    primary = VetPrimary,
    onPrimary = VetOnPrimary,
    primaryContainer = Color(0xFF713F12),
    onPrimaryContainer = Color(0xFFFEF3C7),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0)
)

private val AdminDarkColorScheme = darkColorScheme(
    primary = AdminPrimary,
    onPrimary = AdminOnPrimary,
    primaryContainer = Color(0xFF4C1D95),
    onPrimaryContainer = Color(0xFFEDE9FE),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0)
)

@Composable
fun HuellitasVetTheme(
    isAdmin: Boolean = false,
    role: String = "CLIENTE",
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> {
            when {
                role == "ADMIN" || isAdmin -> AdminDarkColorScheme
                role == "VET" -> VetDarkColorScheme
                else -> ClientDarkColorScheme
            }
        }
        else -> {
            when {
                role == "ADMIN" || isAdmin -> AdminLightColorScheme
                role == "VET" -> VetLightColorScheme
                else -> ClientLightColorScheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
