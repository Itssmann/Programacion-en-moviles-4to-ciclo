package com.abad.saludpluscitas.ui.screens.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.saludpluscitas.data.repository.Repositorio

@Composable
fun PerfilScreen(
    onCerrarSesion: () -> Unit
) {
    val usuario = Repositorio.usuarioActual

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mi perfil",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Nombre: ${usuario?.nombre ?: "Sin datos"}")
                Spacer(modifier = Modifier.height(12.dp))
                Text("Correo: ${usuario?.correo ?: "Sin datos"}")
                Spacer(modifier = Modifier.height(12.dp))
                Text("Teléfono: ${usuario?.telefono ?: "Sin datos"}")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                Repositorio.cerrarSesion()
                onCerrarSesion()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}
