package com.abad.saludpluscitas.ui.screens.agendamiento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.saludpluscitas.data.repository.Repositorio
import com.abad.saludpluscitas.ui.components.BarraSuperior
import com.abad.saludpluscitas.ui.components.BotonSaludPlus
import java.time.LocalDate

@Composable
fun FechaHoraScreen(
    medicoId: Int,
    onContinuar: (String, String) -> Unit,
    onVolver: () -> Unit
) {
    val medico = Repositorio.obtenerMedicoPorId(medicoId)
    val fechas = (1..7).map { LocalDate.now().plusDays(it.toLong()).toString() }
    val horarios = listOf(
        "08:00", "09:00", "10:00", "11:00",
        "14:00", "15:00", "16:00", "17:00"
    )

    var fechaSeleccionada by remember { mutableStateOf("") }
    var horaSeleccionada by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        BarraSuperior(
            titulo = "Fecha y hora",
            onVolver = onVolver
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = medico?.nombre ?: "Médico no encontrado",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text("Selecciona una fecha")

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(160.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(fechas) { fecha ->
                    FilterChip(
                        selected = fechaSeleccionada == fecha,
                        onClick = {
                            fechaSeleccionada = fecha
                            horaSeleccionada = ""
                        },
                        label = { Text(fecha) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Selecciona un horario")

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(220.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(horarios) { hora ->
                    FilterChip(
                        selected = horaSeleccionada == hora,
                        onClick = { horaSeleccionada = hora },
                        label = { Text(hora) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onContinuar(fechaSeleccionada, horaSeleccionada)
                },
                enabled = fechaSeleccionada.isNotEmpty() &&
                        horaSeleccionada.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continuar")
            }
        }
    }
}