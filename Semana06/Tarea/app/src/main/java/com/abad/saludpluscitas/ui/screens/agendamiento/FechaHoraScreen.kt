package com.abad.saludpluscitas.ui.screens.agendamiento

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abad.saludpluscitas.data.repository.Repositorio
import com.abad.saludpluscitas.ui.components.BarraSuperior
import com.abad.saludpluscitas.ui.components.BotonSaludPlus
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun FechaHoraScreen(
    medicoId: Int,
    onContinuar: (String, String) -> Unit,
    onVolver: () -> Unit
) {
    val medico = Repositorio.obtenerMedicoPorId(medicoId)

    var semanaOffset by remember { mutableIntStateOf(0) }
    var fechaSeleccionada by remember { mutableStateOf("") }
    var horaSeleccionada by remember { mutableStateOf("") }

    val fechasList = remember(semanaOffset) {
        obtenerBloqueDiasHabiles(semanaOffset)
    }

    LaunchedEffect(fechasList) {
        if (fechaSeleccionada.isNotEmpty() && fechasList.none { it.toString() == fechaSeleccionada }) {
            fechaSeleccionada = ""
            horaSeleccionada = ""
        }
    }

    val localeEs = remember { Locale.forLanguageTag("es-ES") }
    val formatterDiaSemana = remember { DateTimeFormatter.ofPattern("EEE", localeEs) }
    val formatterDiaNum = remember { DateTimeFormatter.ofPattern("dd MMM", localeEs) }

    val horarios = listOf(
        "08:00", "09:00", "10:00", "11:00",
        "14:00", "15:00", "16:00", "17:00"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        BarraSuperior(
            titulo = "Fecha y hora",
            onVolver = onVolver
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = medico?.nombre ?: "Médico no encontrado",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Selecciona fecha y hora para tu consulta",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = obtenerTextoMesAno(fechasList),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (semanaOffset > 0) {
                                semanaOffset--
                            }
                        },
                        enabled = semanaOffset > 0
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Anterior",
                            tint = if (semanaOffset > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    }

                    IconButton(
                        onClick = {
                            semanaOffset++
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Siguiente",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(fechasList) { localDate ->
                    val fechaIso = localDate.toString()
                    val esSeleccionada = fechaSeleccionada == fechaIso

                    Card(
                        modifier = Modifier
                            .width(82.dp)
                            .clickable {
                                fechaSeleccionada = fechaIso
                                horaSeleccionada = ""
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (esSeleccionada) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (esSeleccionada) 4.dp else 1.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = localDate.format(formatterDiaSemana).uppercase(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (esSeleccionada) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = localDate.format(formatterDiaNum).uppercase(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (esSeleccionada) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Selecciona un horario",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(horarios) { hora ->
                    val esSeleccionada = horaSeleccionada == hora
                    val ocupado = if (fechaSeleccionada.isNotEmpty()) {
                        Repositorio.estaHorarioOcupado(medicoId, fechaSeleccionada, hora)
                    } else {
                        false
                    }
                    val pasado = if (fechaSeleccionada.isNotEmpty()) {
                        estaHoraPasadaHoy(fechaSeleccionada, hora)
                    } else {
                        false
                    }
                    val noDisponible = ocupado || pasado

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !noDisponible && fechaSeleccionada.isNotEmpty()) {
                                horaSeleccionada = hora
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                esSeleccionada -> MaterialTheme.colorScheme.primary
                                noDisponible -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                else -> MaterialTheme.colorScheme.surface
                            }
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (esSeleccionada) 4.dp else 1.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (noDisponible) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = hora,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                    Text(
                                        text = if (pasado) "Pasado" else "Ocupado",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                    )
                                }
                            } else {
                                Text(
                                    text = hora,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (esSeleccionada) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            BotonSaludPlus(
                texto = "Continuar",
                onClick = {
                    onContinuar(fechaSeleccionada, horaSeleccionada)
                },
                enabled = fechaSeleccionada.isNotEmpty() && horaSeleccionada.isNotEmpty()
            )
        }
    }
}

private fun obtenerBloqueDiasHabiles(semanaOffset: Int): List<LocalDate> {
    var actual = LocalDate.now()
    if (actual.dayOfWeek == DayOfWeek.SATURDAY) {
        actual = actual.plusDays(2)
    } else if (actual.dayOfWeek == DayOfWeek.SUNDAY) {
        actual = actual.plusDays(1)
    }

    var saltos = semanaOffset * 5
    while (saltos > 0) {
        actual = actual.plusDays(1)
        if (actual.dayOfWeek != DayOfWeek.SATURDAY && actual.dayOfWeek != DayOfWeek.SUNDAY) {
            saltos--
        }
    }

    val bloque = mutableListOf<LocalDate>()
    while (bloque.size < 5) {
        if ((actual.dayOfWeek != DayOfWeek.SATURDAY) && (actual.dayOfWeek != DayOfWeek.SUNDAY)) {
            bloque.add(actual)
        }
        actual = actual.plusDays(1)
    }
    return bloque
}

private fun obtenerTextoMesAno(dias: List<LocalDate>): String {
    if (dias.isEmpty()) return ""
    val primer = dias.first()
    val ultimo = dias.last()

    val localeEs = Locale.forLanguageTag("es-ES")
    val formatterMes = DateTimeFormatter.ofPattern("MMMM", localeEs)
    val mesPrimer = primer.format(formatterMes).replaceFirstChar { it.uppercase() }
    val mesUltimo = ultimo.format(formatterMes).replaceFirstChar { it.uppercase() }

    return when {
        primer.year != ultimo.year -> {
            "$mesPrimer ${primer.year} - $mesUltimo ${ultimo.year}"
        }
        primer.month != ultimo.month -> {
            "$mesPrimer - $mesUltimo ${primer.year}"
        }
        else -> {
            "$mesPrimer ${primer.year}"
        }
    }
}

private fun estaHoraPasadaHoy(fechaIso: String, horaSlot: String): Boolean {
    val hoyIso = LocalDate.now().toString()
    if (fechaIso != hoyIso) return false
    return try {
        val ahora = LocalTime.now()
        val partes = horaSlot.split(":")
        val horaInt = partes[0].toInt()
        val minInt = partes[1].toInt()
        val slotTime = LocalTime.of(horaInt, minInt)
        slotTime.isBefore(ahora)
    } catch (_: Exception) {
        false
    }
}
