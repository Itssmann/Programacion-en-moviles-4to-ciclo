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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.ui.components.MascotaAvatar
import com.abad.huellitasvet.viewmodel.PedirCitaViewModel
import java.time.LocalDate
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
    val servicioId by viewModel.servicioId.collectAsState()
    val fecha by viewModel.fecha.collectAsState()
    val hora by viewModel.hora.collectAsState()
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
                            color = if (seleccionado) Color(0xFFD1FAE5) else MaterialTheme.colorScheme.surface,
                            border = if (seleccionado) BorderStroke(1.5.dp, Color(0xFF059669)) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                                    color = if (seleccionado) Color(0xFF065F46) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // 2. Servicio
            Text(
                "Servicio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                servicios.forEach { servicio ->
                    val seleccionado = servicioId == servicio.id
                    Surface(
                        onClick = { viewModel.elegirServicio(servicio.id) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (seleccionado) Color(0xFFD1FAE5) else MaterialTheme.colorScheme.surface,
                        border = if (seleccionado) BorderStroke(1.5.dp, Color(0xFF059669)) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFDBEAFE)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = getServiceIcon(servicio.icono),
                                        contentDescription = null,
                                        tint = Color(0xFF1D4ED8),
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
                                    color = if (seleccionado) Color(0xFF065F46) else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${servicio.duracionMin} min",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = "S/ ${"%.0f".format(servicio.precio)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (seleccionado) Color(0xFF065F46) else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // 3. Fecha
            Text(
                "Fecha",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            val dias = remember {
                (0L..6L).map { LocalDate.now().plusDays(it) }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dias) { dia ->
                    val seleccionado = fecha == dia
                    val diaSemana = dia.format(DateTimeFormatter.ofPattern("EEE", Locale.forLanguageTag("es")))
                    val numeroDia = dia.format(DateTimeFormatter.ofPattern("dd"))

                    Surface(
                        onClick = { viewModel.elegirFecha(dia) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (seleccionado) Color(0xFF059669) else MaterialTheme.colorScheme.surface,
                        border = if (!seleccionado) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
                        modifier = Modifier.width(60.dp).height(64.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = diaSemana.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.forLanguageTag("es")) else it.toString() },
                                style = MaterialTheme.typography.bodySmall,
                                color = if (seleccionado) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = numeroDia,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (seleccionado) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 4. Hora
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Hora",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Ocupado = gris",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.horarios.forEach { opcion ->
                    val ocupada = opcion in ocupadas
                    val pasada = !viewModel.horaDisponible(opcion) && !ocupada
                    val deshabilitado = ocupada || pasada
                    val seleccionado = hora == opcion

                    Surface(
                        onClick = { if (!deshabilitado) viewModel.elegirHora(opcion) },
                        enabled = !deshabilitado,
                        modifier = Modifier
                            .width(76.dp)
                            .height(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            seleccionado -> Color(0xFF059669)
                            deshabilitado -> Color(0xFFF1F5F9)
                            else -> MaterialTheme.colorScheme.surface
                        },
                        border = if (!seleccionado && !deshabilitado) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = opcion,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = when {
                                    seleccionado -> Color.White
                                    deshabilitado -> Color.Gray
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                                textDecoration = if (deshabilitado) TextDecoration.LineThrough else null
                            )
                        }
                    }
                }
            }

            // Resumen card (very light green)
            if (mascotaElegida != null || servicioElegido != null || hora != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFD1FAE5)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${mascotaElegida?.nombre ?: "Mascota"} · ${servicioElegido?.nombre ?: "Servicio"}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF065F46)
                        )
                        val formatoFecha = fecha.format(DateTimeFormatter.ofPattern("EEE dd MMM", Locale.forLanguageTag("es")))
                        val textoFecha = formatoFecha.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.forLanguageTag("es")) else it.toString() }
                        Text(
                            text = "$textoFecha · ${hora ?: "--:--"} · S/ ${"%.2f".format(servicioElegido?.precio ?: 0.0)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF047857),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            error?.let {
                Text(
                    it,
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
                        servicioId != null &&
                        hora != null &&
                        !guardando &&
                        aceptarCitas,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF059669),
                    contentColor = Color.White
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
