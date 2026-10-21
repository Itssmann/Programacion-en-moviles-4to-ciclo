package com.abad.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AjustesScreen(
    nombre: String,
    correo: String,
    modoOscuro: Boolean,
    notificaciones: Boolean,
    aceptarCitas: Boolean,
    onModoOscuro: (Boolean) -> Unit,
    onNotificaciones: (Boolean) -> Unit,
    onAceptarCitas: (Boolean) -> Unit,
    onCambiarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TextButton(onClick = onVolver) { Text("← Panel") }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Ajustes", style = MaterialTheme.typography.headlineMedium)
            Text(nombre, style = MaterialTheme.typography.titleLarge)
            Text(correo)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Modo oscuro")
                Switch(
                    checked = modoOscuro,
                    onCheckedChange = onModoOscuro
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Notificaciones")
                Switch(
                    checked = notificaciones,
                    onCheckedChange = onNotificaciones
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Aceptar citas")
                Switch(
                    checked = aceptarCitas,
                    onCheckedChange = onAceptarCitas
                )
            }

            if (!aceptarCitas) {
                Text(
                    "La clínica no está recibiendo citas",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = onCambiarPerfil,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cambiar de perfil")
            }

            OutlinedButton(
                onClick = onCerrarSesion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}

