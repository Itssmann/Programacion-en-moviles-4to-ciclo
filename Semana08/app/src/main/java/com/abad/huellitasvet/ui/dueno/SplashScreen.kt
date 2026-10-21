package com.abad.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.abad.huellitasvet.data.prefs.PreferenciasStore
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(
    preferencias: PreferenciasStore,
    onDestino: (String) -> Unit
) {
    LaunchedEffect(preferencias) {
        val rol = preferencias.perfilActivo.first()
        val id = preferencias.usuarioId.first()

        val destino = when {
            id <= 0 -> "login"
            rol == "CLIENTE" -> "inicio"
            rol == "ADMIN" -> "panel"
            else -> "login"
        }

        onDestino(destino)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

