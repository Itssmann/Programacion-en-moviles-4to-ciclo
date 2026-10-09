package com.abad.saludpluscitas.ui.screens.agendamiento

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abad.saludpluscitas.data.repository.Repositorio
import com.abad.saludpluscitas.ui.components.BarraSuperior
import com.abad.saludpluscitas.ui.components.BotonSaludPlus
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ConfirmarCitaScreen(
    medicoId: Int,
    fecha: String,
    hora: String,
    onConfirmar: () -> Unit,
    onVolver: () -> Unit
) {
    val medico = Repositorio.obtenerMedicoPorId(medicoId)
    val estaOcupado = Repositorio.estaHorarioOcupado(medicoId, fecha, hora)
    var estaProcesando by remember { mutableStateOf(false) }
    var errorConflicto by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        BarraSuperior(
            titulo = "Confirmar cita",
            onVolver = onVolver
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Resumen de tu cita",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Por favor verifica los detalles antes de confirmar.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

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
                        FilaDetalleConfirmacion(
                            icono = Icons.Default.Person,
                            titulo = "Médico",
                            subtitulo = medico?.nombre ?: "No encontrado",
                            fotoResId = medico?.fotoResId
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        FilaDetalleConfirmacion(
                            icono = Icons.Default.CalendarMonth,
                            titulo = "Fecha",
                            subtitulo = formatearFechaEspanol(fecha)
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        FilaDetalleConfirmacion(
                            icono = Icons.Default.Schedule,
                            titulo = "Hora",
                            subtitulo = hora
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        FilaDetalleConfirmacion(
                            icono = Icons.Default.LocationOn,
                            titulo = "Lugar",
                            subtitulo = Repositorio.sedeActual?.let { "Clínica SaludPlus - ${it.nombre} (${it.direccion})" } ?: "Clínica SaludPlus - Sede Principal"
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        FilaDetalleConfirmacion(
                            icono = Icons.Default.Info,
                            titulo = "Estado",
                            subtitulo = if (estaOcupado || errorConflicto) "Horario no disponible" else "Por confirmar"
                        )
                    }
                }

                if (estaOcupado || errorConflicto) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "El médico ya no está disponible en este horario. Selecciona otro",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            }

            BotonSaludPlus(
                texto = if (estaProcesando) "Procesando..." else "Confirmar cita",
                onClick = {
                    if (!estaProcesando && !estaOcupado && !errorConflicto) {
                        if (Repositorio.estaHorarioOcupado(medicoId, fecha, hora)) {
                            errorConflicto = true
                        } else {
                            estaProcesando = true
                            onConfirmar()
                        }
                    }
                },
                enabled = !estaProcesando && !estaOcupado && !errorConflicto
            )
        }
    }
}

private fun formatearFechaEspanol(fechaIso: String): String {
    return try {
        val localDate = LocalDate.parse(fechaIso)
        val localeEs = Locale.forLanguageTag("es-ES")
        val formatter = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", localeEs)
        val texto = localDate.format(formatter)
        texto.replaceFirstChar { it.uppercase() }
    } catch (_: Exception) {
        fechaIso
    }
}

@Composable
private fun FilaDetalleConfirmacion(
    icono: ImageVector,
    titulo: String,
    subtitulo: String,
    fotoResId: Int? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (fotoResId != null) {
                    Image(
                        painter = painterResource(id = fotoResId),
                        contentDescription = null,
                        modifier = Modifier
                            .size(44.dp)
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
                text = titulo,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subtitulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
