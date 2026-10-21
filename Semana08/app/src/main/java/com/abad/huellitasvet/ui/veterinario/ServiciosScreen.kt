    package com.abad.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.viewmodel.ServiciosViewModel

@Composable
fun ServiciosScreen(
    viewModel: ServiciosViewModel,
    onVolver: () -> Unit,
    onNuevo: () -> Unit,
    onEditar: (Int) -> Unit
) {
    val servicios by viewModel.servicios.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    Scaffold(
        topBar = {
            TextButton(onClick = onVolver) { Text("← Panel") }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevo) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Servicios",
                style = MaterialTheme.typography.headlineMedium
            )

            mensaje?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(servicios, key = { it.id }) { servicio ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "${servicio.icono} ${servicio.nombre}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(servicio.descripcion)
                            Text(
                                "${servicio.duracionMin} min · " +
                                        "S/ ${"%.2f".format(servicio.precio)}"
                            )
                            Text(
                                if (servicio.activo) "Activo" else "Inactivo",
                                color = if (servicio.activo)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error
                            )

                            Row {
                                TextButton(
                                    onClick = { onEditar(servicio.id) }
                                ) {
                                    Text("Editar")
                                }
                                TextButton(
                                    onClick = { viewModel.eliminar(servicio) }
                                ) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

