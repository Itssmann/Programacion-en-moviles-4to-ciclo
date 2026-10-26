package com.abad.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.data.local.entity.Cita
import com.abad.huellitasvet.ui.components.CitaCard
import com.abad.huellitasvet.viewmodel.MisCitasViewModel
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MisCitasScreen(
    viewModel: MisCitasViewModel,
    onPedirCita: () -> Unit,
    bottomBar: @Composable () -> Unit = {}
) {
    val proximas by viewModel.proximas.collectAsState()
    val historial by viewModel.historial.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    var pestana by remember { mutableIntStateOf(0) }
    var citaACancelar by remember { mutableStateOf<Cita?>(null) }
    var citaAReprogramar by remember { mutableStateOf<Cita?>(null) }
    var nuevaFecha by remember { mutableStateOf(LocalDate.now()) }
    var nuevaHora by remember { mutableStateOf("09:00") }

    LaunchedEffect(Unit) {
        viewModel.actualizarHora()
    }

    if (citaACancelar != null) {
        AlertDialog(
            onDismissRequest = { citaACancelar = null },
            title = { Text("Cancelar cita") },
            text = { Text("¿Seguro que deseas cancelar esta cita?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        citaACancelar?.let(viewModel::cancelar)
                        citaACancelar = null
                    }
                ) {
                    Text("Sí, cancelar")
                }
            },
            dismissButton = {
                TextButton(onClick = { citaACancelar = null }) {
                    Text("Volver")
                }
            }
        )
    }

    if (citaAReprogramar != null) {
        val dias = remember { (0L..6L).map { LocalDate.now().plusDays(it) } }
        val horas = com.abad.huellitasvet.model.Horarios.horas

        AlertDialog(
            onDismissRequest = { citaAReprogramar = null },
            title = { Text("Reprogramar cita") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Selecciona nueva fecha:")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(dias) { dia ->
                            FilterChip(
                                selected = nuevaFecha == dia,
                                onClick = { nuevaFecha = dia },
                                label = { Text("${dia.dayOfMonth}/${dia.monthValue}") }
                            )
                        }
                    }

                    Text("Selecciona nueva hora:")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        horas.forEach { horaOpcion ->
                            FilterChip(
                                selected = nuevaHora == horaOpcion,
                                onClick = { nuevaHora = horaOpcion },
                                label = { Text(horaOpcion) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        citaAReprogramar?.let {
                            viewModel.reprogramar(it.id, nuevaFecha, nuevaHora)
                        }
                        citaAReprogramar = null
                    }
                ) {
                    Text("Confirmar reprogramación")
                }
            },
            dismissButton = {
                TextButton(onClick = { citaAReprogramar = null }) {
                    Text("Volver")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Column {
                Text(
                    "Mis citas",
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 12.dp),
                    style = MaterialTheme.typography.headlineMedium
                )
                TabRow(selectedTabIndex = pestana) {
                    Tab(
                        selected = pestana == 0,
                        onClick = {
                            viewModel.actualizarHora()
                            pestana = 0
                        },
                        text = { Text("Próximas") }
                    )
                    Tab(
                        selected = pestana == 1,
                        onClick = {
                            viewModel.actualizarHora()
                            pestana = 1
                        },
                        text = { Text("Historial") }
                    )
                }
            }
        },
        bottomBar = bottomBar,
        floatingActionButton = {
            FloatingActionButton(onClick = onPedirCita) {
                Text("+")
            }
        }
    ) { padding ->
        val lista = if (pestana == 0) proximas else historial

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (pestana == 0) {
                Text(
                    "Puedes cancelar o reprogramar hasta 2 horas antes",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            mensaje?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
                TextButton(onClick = viewModel::limpiarMensaje) {
                    Text("Cerrar aviso")
                }
            }

            if (lista.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (pestana == 0) "No tienes citas próximas"
                        else "Aún no tienes historial de citas"
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(lista, key = { it.cita.id }) { detalle ->
                        CitaCard(
                            detalle = detalle,
                            mostrarCancelar = pestana == 0 &&
                                    viewModel.puedeCancelar(detalle.cita),
                            onCancelar = {
                                citaACancelar = detalle.cita
                            },
                            mostrarReprogramar = pestana == 0 &&
                                    viewModel.puedeReprogramar(detalle.cita),
                            onReprogramar = {
                                citaAReprogramar = detalle.cita
                            }
                        )
                    }
                }
            }
        }
    }
}
