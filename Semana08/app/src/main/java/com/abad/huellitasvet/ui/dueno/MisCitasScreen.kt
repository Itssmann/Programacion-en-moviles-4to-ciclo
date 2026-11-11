package com.abad.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.data.local.entity.Cita
import com.abad.huellitasvet.ui.components.CitaCard
import com.abad.huellitasvet.viewmodel.MisCitasViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
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
    var nuevaFecha by remember { mutableStateOf(LocalDate.now().plusDays(1)) }
    var nuevaHora by remember { mutableStateOf("09:00") }

    var showDatePickerReprogramar by remember { mutableStateOf(false) }
    val datePickerReprogramarState = rememberDatePickerState(
        initialSelectedDateMillis = nuevaFecha.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    if (showDatePickerReprogramar) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerReprogramar = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerReprogramarState.selectedDateMillis?.let { millis ->
                        val selected = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        if (!selected.isBefore(LocalDate.now())) {
                            nuevaFecha = selected
                        }
                    }
                    showDatePickerReprogramar = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerReprogramar = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerReprogramarState)
        }
    }

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
        val horas = com.abad.huellitasvet.model.Horarios.horas

        AlertDialog(
            onDismissRequest = { citaAReprogramar = null },
            title = { Text("Reprogramar cita") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Selecciona nueva fecha:")
                    OutlinedButton(
                        onClick = { showDatePickerReprogramar = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(nuevaFecha.format(DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es"))))
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
        bottomBar = bottomBar
    ) { padding ->
        val lista = if (pestana == 0) proximas else historial

        if (lista.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        if (pestana == 0) "No tienes citas próximas" else "No hay historial de citas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (pestana == 0) {
                        Button(
                            onClick = onPedirCita,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Pedir una cita")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lista, key = { it.cita.id }) { detalle ->
                    CitaCard(
                        detalle = detalle,
                        mostrarCancelar = viewModel.puedeCancelar(detalle.cita),
                        onCancelar = { citaACancelar = detalle.cita },
                        mostrarReprogramar = viewModel.puedeReprogramar(detalle.cita),
                        onReprogramar = {
                            nuevaFecha = runCatching { LocalDate.parse(detalle.cita.fecha) }.getOrDefault(LocalDate.now().plusDays(1))
                            nuevaHora = detalle.cita.hora
                            citaAReprogramar = detalle.cita
                        }
                    )
                }
            }
        }
    }
}
