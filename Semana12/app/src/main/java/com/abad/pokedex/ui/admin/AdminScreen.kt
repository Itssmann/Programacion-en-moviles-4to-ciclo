package com.abad.pokedex.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdminScreen(
    totalPokemones: Int,
    totalFavoritos: Int,
    onLimpiarCache: () -> Unit,
    onSincronizar: () -> Unit,
    cargando: Boolean = false,
    mensaje: String? = null
) {
    var mostrarConfirmacion by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Panel de administración",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Resumen de datos",
                    style = MaterialTheme.typography.titleMedium
                )

                Text("Pokémon almacenados: $totalPokemones")
                Text("Favoritos: $totalFavoritos")
            }
        }

        Button(
            onClick = onSincronizar,
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sincronizar Pokémon")
        }

        OutlinedButton(
            onClick = { mostrarConfirmacion = true },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Limpiar caché")
        }

        if (cargando) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
            Text("Procesando operación...")
        }

        if (mensaje != null) {
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = {
                mostrarConfirmacion = false
            },
            title = {
                Text("¿Limpiar caché?")
            },
            text = {
                Text(
                    "Se eliminarán los Pokémon descargados " +
                            "de este dispositivo. Los favoritos " +
                            "seguirán registrados."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacion = false
                        onLimpiarCache()
                    }
                ) {
                    Text("Limpiar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacion = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
