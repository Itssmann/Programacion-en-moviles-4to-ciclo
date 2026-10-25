package com.abad.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.ui.components.EstadoCitaChip
import com.abad.huellitasvet.ui.components.MascotaAvatar
import com.abad.huellitasvet.viewmodel.AgendaViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    viewModel: AgendaViewModel,
    onAtender: (Int) -> Unit,
    bottomBar: @Composable () -> Unit = {}
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
                    Text("Sí, cancelar")
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
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Agenda",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    Spacer(Modifier.width(48.dp))
                }
            )
        },
        bottomBar = bottomBar
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val dias = remember {
                (0L..6L).map { LocalDate.now().plusDays(it) }
            }

            // Horizontal date selector
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(dias) { dia ->
                    val esHoy = dia == LocalDate.now()
                    val label = if (esHoy) {
                        "Hoy ${dia.dayOfMonth}"
                    } else {
                        val formato = DateTimeFormatter.ofPattern("EEE dd", Locale.forLanguageTag("es"))
                        dia.format(formato).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.forLanguageTag("es")) else it.toString() }
                    }

                    FilterChip(
                        selected = fecha == dia,
                        onClick = { viewModel.seleccionarFecha(dia) },
                        label = { Text(label, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            mensaje?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
                TextButton(onClick = viewModel::limpiarMensaje) {
                    Text("Cerrar")
                }
            }

            if (citas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay citas para esta fecha")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(citas, key = { it.cita.id }) { detalle ->
                        val cita = detalle.cita

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Time on the left
                            Text(
                                text = cita.hora,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.width(55.dp)
                            )

                            // Appointment Card on the right
                            ElevatedCard(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        MascotaAvatar(
                                            especie = detalle.mascota.mascota.especie,
                                            modifier = Modifier.size(44.dp)
                                        )
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                text = "${detalle.mascota.mascota.nombre} · ${detalle.servicio.nombre}",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Dueño: ${detalle.mascota.dueno.nombre}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        EstadoCitaChip(cita.estado)

                                        when (cita.estado) {
                                            "PENDIENTE" -> {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    TextButton(
                                                        onClick = { confirmarCancelacion = cita },
                                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                                    ) {
                                                        Text("Cancelar", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                                    }
                                                    Button(
                                                        onClick = { viewModel.confirmar(cita) },
                                                        shape = RoundedCornerShape(10.dp),
                                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                                    ) {
                                                        Text("Confirmar", style = MaterialTheme.typography.bodySmall)
                                                    }
                                                }
                                            }
                                            "CONFIRMADA" -> {
                                                Button(
                                                    onClick = { onAtender(cita.id) },
                                                    shape = RoundedCornerShape(10.dp),
                                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                                                ) {
                                                    Text("Atender", style = MaterialTheme.typography.bodySmall)
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
        }
    }
}
