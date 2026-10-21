package com.abad.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.ui.components.EstadoCitaChip
import com.abad.huellitasvet.ui.components.MascotaAvatar
import com.abad.huellitasvet.viewmodel.AgendaViewModel
import java.time.LocalDate

@Composable
fun AgendaScreen(
    viewModel: AgendaViewModel,
    onVolver: () -> Unit,
    onAtender: (Int) -> Unit
) {
    val fecha by viewModel.fechaSeleccionada.collectAsState()
    val citas by viewModel.citas.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    var confirmarCancelacion by remember {
        mutableStateOf<com.abad.huellitasvet.data.local.entity.Cita?>(null)
    }

    if (confirmarCancelacion != null) {
        AlertDialog(
            onDismissRequest = { confirmarCancelacion = null },
            title = { Text("Cancelar cita") },
            text = { Text("¿Deseas cancelar esta cita?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarCancelacion?.let(viewModel::cancelar)
                    confirmarCancelacion = null
                }) {
                    Text("Cancelar cita")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarCancelacion = null }) {
                    Text("Volver")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Column {
                TextButton(onClick = onVolver) {
                    Text("← Panel")
                }
                Text(
                    "Agenda de la clínica",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    style = MaterialTheme.typography.headlineMedium
                )
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
            val dias = remember {
                (0L..6L).map { LocalDate.now().plusDays(it) }
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(dias) { dia ->
                    FilterChip(
                        selected = fecha == dia,
                        onClick = { viewModel.seleccionarFecha(dia) },
                        label = { Text("${dia.dayOfMonth}/${dia.monthValue}") }
                    )
                }
            }

            mensaje?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
                TextButton(onClick = viewModel::limpiarMensaje) {
                    Text("Cerrar")
                }
            }

            Text("${citas.size} citas para $fecha")

            if (citas.isEmpty()) {
                Text("No hay citas para esta fecha")
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(citas, key = { it.cita.id }) { detalle ->
                    val cita = detalle.cita

                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                "🕘 ${cita.hora}",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                MascotaAvatar(detalle.mascota.mascota.especie)
                                Column {
                                    Text(detalle.mascota.mascota.nombre)
                                    Text(detalle.servicio.nombre)
                                    Text("Dueño: ${detalle.mascota.dueno.nombre}")
                                }
                            }

                            EstadoCitaChip(cita.estado)

                            when (cita.estado) {
                                "PENDIENTE" -> {
                                    Button(
                                        onClick = { viewModel.confirmar(cita) }
                                    ) {
                                        Text("Confirmar")
                                    }
                                    TextButton(
                                        onClick = {
                                            confirmarCancelacion = cita
                                        }
                                    ) {
                                        Text("Cancelar")
                                    }
                                }
                                "CONFIRMADA" -> {
                                    Button(onClick = { onAtender(cita.id) }) {
                                        Text("Atender")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
