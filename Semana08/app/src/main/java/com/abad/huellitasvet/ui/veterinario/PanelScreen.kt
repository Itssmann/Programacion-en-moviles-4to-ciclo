package com.abad.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.viewmodel.PanelViewModel
@Composable
fun PanelScreen(
    viewModel: PanelViewModel,
    onAgenda: () -> Unit,
    onPacientes: () -> Unit,
    onServicios: () -> Unit,
    onAjustes: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val citasHoy by viewModel.citasHoy.collectAsState()
    val pendientes by viewModel.pendientes.collectAsState()
    val atendidas by viewModel.atendidas.collectAsState()
    val ingresos by viewModel.ingresosMes.collectAsState()
    val estados by viewModel.porEstado.collectAsState()
    val servicios by viewModel.porServicio.collectAsState()

    Scaffold(
        topBar = {
            Text(
                "Panel veterinario",
                modifier = Modifier.padding(20.dp),
                style = MaterialTheme.typography.headlineMedium
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Triple("Citas hoy", citasHoy, Color(0xFF2563EB)),
                    Triple("Pendientes", pendientes, Color(0xFFD97706)),
                    Triple("Atendidas", atendidas, Color(0xFF16A34A))
                ).forEach { (titulo, cantidad, color) ->
                    ElevatedCard(modifier = Modifier.weight(1f)) {
                        Column(Modifier.padding(10.dp)) {
                            Text(
                                "$cantidad",
                                color = color,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                titulo,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color(0xFF1D4ED8)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Ingresos del mes", color = Color.White)
                    Text(
                        "S/ ${"%.2f".format(ingresos)}",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            if (pendientes > 0) {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Tienes $pendientes citas pendientes")
                        Button(onClick = onAgenda) {
                            Text("Ver agenda")
                        }
                    }
                }
            }

            Text(
                "Citas por estado",
                style = MaterialTheme.typography.titleLarge
            )

            val total = estados.sumOf { it.second }.coerceAtLeast(1)
            estados.forEach { (nombre, cantidad) ->
                Text("$nombre: $cantidad")
                LinearProgressIndicator(
                    progress = { cantidad.toFloat() / total },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(
                "Citas por servicio",
                style = MaterialTheme.typography.titleLarge
            )

            servicios.forEach { servicio ->
                Text(servicio.toString())
            }


            Button(
                onClick = onAgenda,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Abrir agenda")
            }

            Button(
                onClick = onPacientes,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver pacientes")
            }

            Button(
                onClick = onServicios,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Administrar servicios")
            }

            Button(
                onClick = onAjustes,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ajustes")
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
