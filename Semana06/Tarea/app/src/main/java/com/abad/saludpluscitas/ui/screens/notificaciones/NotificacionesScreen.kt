
package com.abad.saludpluscitas.ui.screens.notificaciones

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.saludpluscitas.ui.components.BarraSuperior

@Composable
fun NotificacionesScreen(
    onVolver: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        BarraSuperior(
            titulo = "Notificaciones",
            onVolver = onVolver
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Text("No tienes notificaciones por el momento.")
        }
    }
}

