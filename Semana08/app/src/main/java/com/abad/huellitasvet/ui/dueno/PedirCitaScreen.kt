package com.abad.huellitasvet.ui.dueno

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.ui.components.MascotaAvatar
import com.abad.huellitasvet.viewmodel.PedirCitaViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun getServiceIcon(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "heart" -> Icons.Default.Favorite
        "syringe", "vacuna", "vacunas" -> Icons.Default.Healing
        "scissors", "baño", "corte", "baño y corte" -> Icons.Default.ContentCut
        else -> Icons.Default.Pets
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PedirCitaScreen(
    viewModel: PedirCitaViewModel,
    onVolver: () -> Unit,
    onConfirmada: () -> Unit
) {
    val mascotas by viewModel.mascotas.collectAsState()
    val servicios by viewModel.servicios.collectAsState()
    val mascotaId by viewModel.mascotaId.collectAsState()
    val servicioIds by viewModel.servicioIds.collectAsState()
    val fecha by viewModel.fecha.collectAsState()
    val hora by viewModel.hora.collectAsState()
    val error by viewModel.error.collectAsState()
    val confirmada by viewModel.confirmada.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val aceptarCitas by viewModel.aceptarCitas.collectAsState()

    val serviciosElegidos = servicios.filter { it.id in servicioIds }
    val precioTotal = serviciosElegidos.sumOf { it.precio }

    var showDatePickerCita by remember { mutableStateOf(false) }
    val datePickerCitaState = rememberDatePickerState(
        initialSelectedDateMillis = fecha.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    if (showDatePickerCita) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerCita = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerCitaState.selectedDateMillis?.let { millis ->
                        val selected = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        if (!selected.isBefore(LocalDate.now())) {
                            viewModel.elegirFecha(selected)
                        }
                    }
                    showDatePickerCita = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerCita = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerCitaState)
        }
    }

    LaunchedEffect(confirmada) {
        if (confirmada) onConfirmada()
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
                            "Pedir cita",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    Spacer(Modifier.width(48.dp))
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. ¿Quién viene? (Mascotas)
            Text(
                "¿Quién viene?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (mascotas.isEmpty()) {
                Text("Primero registra una mascota.")
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    mascotas.forEach { mascota ->
                        val seleccionado = mascotaId == mascota.id
                        Surface(
                            onClick = { viewModel.elegirMascota(mascota.id) },
                            shape = RoundedCornerShape(24.dp),
                            color = if (seleccionado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MascotaAvatar(especie = mascota.especie, modifier = Modifier.size(32.dp))
                                Text(
                                    text = mascota.nombre,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (seleccionado) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // 2. Servicios
            Text(
                "Servicios (selecciona uno o más)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                servicios.forEach { servicio ->
                    val seleccionado = servicio.id in servicioIds
                    Surface(
                        onClick = { viewModel.toggleServicio(servicio.id) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (seleccionado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Checkbox(
                                checked = seleccionado,
                                onCheckedChange = { viewModel.toggleServicio(servicio.id) }
                            )

                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = getServiceIcon(servicio.icono),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = servicio.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (seleccionado) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${servicio.duracionMin} min · ${servicio.descripcion}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = "S/ ${"%.2f".format(servicio.precio)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            if (serviciosElegidos.isNotEmpty()) {
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Resumen de servicios:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        serviciosElegidos.forEach { s ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("• ${s.nombre}", style = MaterialTheme.typography.bodySmall)
                                Text("S/ ${"%.2f".format(s.precio)}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total estimado:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("S/ ${"%.2f".format(precioTotal)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // 3. Fecha interactiva (DatePicker M3)
            Text(
                "Fecha de la cita",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedCard(
                onClick = { showDatePickerCita = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Fecha seleccionada", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = fecha.format(DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es"))),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 4. Hora
            Text(
                "Hora disponible",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.horarios.forEach { h ->
                    val disponible = viewModel.horaDisponible(h)
                    val seleccionado = hora == h

                    FilterChip(
                        selected = seleccionado,
                        onClick = { if (disponible) viewModel.elegirHora(h) },
                        enabled = disponible,
                        label = { Text(h) }
                    )
                }
            }

            // 5. Nota adicional
            OutlinedTextField(
                value = viewModel.nota.collectAsState().value,
                onValueChange = viewModel::escribirNota,
                label = { Text("Nota adicional o síntomas (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )

            if (error != null) {
                Text(
                    error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (!aceptarCitas) {
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "La clínica no está aceptando nuevas citas en este momento",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Button(
                onClick = viewModel::confirmar,
                enabled = mascotaId != null &&
                        servicioIds.isNotEmpty() &&
                        hora != null &&
                        !guardando &&
                        aceptarCitas,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    if (guardando) "Reservando..." else "Confirmar cita",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
