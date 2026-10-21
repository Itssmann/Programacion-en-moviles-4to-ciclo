package com.abad.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PerfilScreen(
    nombre: String,
    correo: String,
    cantidadMascotas: Int,
    citasProximas: Int,
    atenciones: Int,
    modoOscuro: Boolean,
    notificaciones: Boolean,
    onModoOscuro: (Boolean) -> Unit,
    onNotificaciones: (Boolean) -> Unit,
    onCambiarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TextButton(onClick = onVolver) { Text("← Inicio") }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Mi perfil", style = MaterialTheme.typography.headlineMedium)
            Text(nombre, style = MaterialTheme.typography.titleLarge)
            Text(correo)

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Mis mascotas: $cantidadMascotas")
                    Text("Próximas citas: $citasProximas")
                    Text("Atenciones: $atenciones")
                }
            }

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
                Text("Recordatorios")
                Switch(
                    checked = notificaciones,
                    onCheckedChange = onNotificaciones
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
