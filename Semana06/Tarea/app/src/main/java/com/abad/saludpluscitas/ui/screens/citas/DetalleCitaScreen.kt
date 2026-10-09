package com.abad.saludpluscitas.ui.screens.citas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abad.saludpluscitas.data.repository.Repositorio
import com.abad.saludpluscitas.ui.components.BarraSuperior

@Composable
fun DetalleCitaScreen(
    citaId: Int,
    onVolver: () -> Unit
) {
    val usuarioId = Repositorio.usuarioActual?.id ?: 1
    val cita = Repositorio.citas.find { it.id == citaId && it.usuarioId == usuarioId }
    val medico = cita?.let {
        Repositorio.obtenerMedicoPorId(it.medicoId)
    }
    val especialidad = medico?.let {
        Repositorio.especialidades.find { esp -> esp.id == it.especialidadId }?.nombre
    } ?: "Medicina General"

    var mostrarDialogoCancelar by remember { mutableStateOf(false) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }

    val esPasada = cita != null && Repositorio.esCitaPasada(cita.fecha, cita.hora)
    val esConfirmada = cita != null && cita.estado.equals("Confirmada", true)
    val puedeCancelar = esConfirmada && !esPasada

    Column(modifier = Modifier.fillMaxSize()) {
        BarraSuperior(
            titulo = "Detalle de cita",
            onVolver = onVolver
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                if (cita == null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No se encontró la cita solicitada.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            FilaDetalleCita(
                                icono = Icons.Default.Person,
                                etiqueta = "Médico tratante",
                                valor = medico?.nombre ?: "No encontrado",
                                fotoResId = medico?.fotoResId
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            FilaDetalleCita(
                                icono = Icons.Default.Info,
                                etiqueta = "Especialidad",
                                valor = especialidad
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            FilaDetalleCita(
                                icono = Icons.Default.CalendarMonth,
                                etiqueta = "Fecha",
                                valor = cita.fecha
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            FilaDetalleCita(
                                icono = Icons.Default.Schedule,
                                etiqueta = "Hora",
                                valor = cita.hora
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            FilaDetalleCita(
                                icono = Icons.Default.LocationOn,
                                etiqueta = "Sede",
                                valor = cita.sede
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            FilaDetalleCita(
                                icono = Icons.Default.Info,
                                etiqueta = "Estado",
                                valor = cita.estado
                            )
                        }
                    }

                    if (mensajeExito != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = mensajeExito ?: "",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }

            if (cita != null && puedeCancelar && mensajeExito == null) {
                Button(
                    onClick = { mostrarDialogoCancelar = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Cancelar cita",
                        color = MaterialTheme.colorScheme.onError,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }

    if (mostrarDialogoCancelar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCancelar = false },
            title = { Text("Cancelar cita") },
            text = { Text("¿Estás seguro de que deseas cancelar esta cita?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoCancelar = false
                        val exito = Repositorio.cancelarCita(citaId, usuarioId)
                        if (exito) {
                            mensajeExito = "Tu cita ha sido cancelada correctamente"
                        }
                    }
                ) {
                    Text("Sí, cancelar cita", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoCancelar = false }
                ) {
                    Text("Volver")
                }
            }
        )
    }
}

@Composable
private fun FilaDetalleCita(
    icono: ImageVector,
    etiqueta: String,
    valor: String,
    fotoResId: Int? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (fotoResId != null) {
                    Image(
                        painter = painterResource(id = fotoResId),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = etiqueta,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = valor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
