package com.abad.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.viewmodel.PedirCitaViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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
    val servicioId by viewModel.servicioId.collectAsState()
    val fecha by viewModel.fecha.collectAsState()
    val hora by viewModel.hora.collectAsState()
    val nota by viewModel.nota.collectAsState()
    val ocupadas by viewModel.horasOcupadas.collectAsState()
    val error by viewModel.error.collectAsState()
    val confirmada by viewModel.confirmada.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val aceptarCitas by viewModel.aceptarCitas.collectAsState()

    val servicioElegido = servicios.find { it.id == servicioId }
    val mascotaElegida = mascotas.find { it.id == mascotaId }

    LaunchedEffect(confirmada) {
        if (confirmada) onConfirmada()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pedir cita") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "Pedir una cita",
                style = MaterialTheme.typography.headlineMedium
            )
            Text("Selecciona tu mascota, servicio y horario.")

            Text(
                "1. Tu mascota",
                style = MaterialTheme.typography.titleMedium
            )
            if (mascotas.isEmpty()) {
                Text("Primero registra una mascota.")
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    mascotas.forEach { mascota ->
                        FilterChip(
                            selected = mascotaId == mascota.id,
                            onClick = { viewModel.elegirMascota(mascota.id) },
                            label = { Text(mascota.nombre) }
                        )
                    }
                }
            }

            Text(
                "2. Servicio",
                style = MaterialTheme.typography.titleMedium
            )
            servicios.forEach { servicio ->
                ElevatedCard(
                    onClick = { viewModel.elegirServicio(servicio.id) },
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (servicioId == servicio.id) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerLow
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            servicio.nombre,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "${servicio.duracionMin} min · S/ ${"%.2f".format(servicio.precio)}"
                        )
                        Text(
                            servicio.descripcion,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Text(
                "3. Fecha",
                style = MaterialTheme.typography.titleMedium
            )

            val dias = remember {
                (0L..7L).map { LocalDate.now().plusDays(it) }
            }
            val formato = remember {
                DateTimeFormatter.ofPattern("EEE dd", Locale("es", "PE"))
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dias) { dia ->
                    FilterChip(
                        selected = fecha == dia,
                        onClick = { viewModel.elegirFecha(dia) },
                        label = { Text(dia.format(formato)) }
                    )
                }
            }

            Text(
                "4. Hora disponible",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "Los horarios ocupados o pasados están deshabilitados.",
                style = MaterialTheme.typography.bodySmall
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                viewModel.horarios.forEach { opcion ->
                    val disponible = viewModel.horaDisponible(opcion) &&
                            opcion !in ocupadas

                    FilterChip(
                        selected = hora == opcion,
                        onClick = { viewModel.elegirHora(opcion) },
                        enabled = disponible,
                        label = { Text(opcion) }
                    )
                }
            }

            OutlinedTextField(
                value = nota,
                onValueChange = viewModel::escribirNota,
                label = { Text("Nota para el veterinario (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        "Resumen de tu cita",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("Mascota: ${mascotaElegida?.nombre ?: "Sin elegir"}")
                    Text("Servicio: ${servicioElegido?.nombre ?: "Sin elegir"}")
                    Text("Fecha: $fecha")
                    Text("Hora: ${hora ?: "Sin elegir"}")
                    Text("Total: S/ ${"%.2f".format(servicioElegido?.precio ?: 0.0)}")
                }
            }

            error?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error
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
                        servicioId != null &&
                        hora != null &&
                        !guardando &&
                        aceptarCitas,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(if (guardando) "Reservando..." else "Confirmar cita")
            }
        }
    }
}
