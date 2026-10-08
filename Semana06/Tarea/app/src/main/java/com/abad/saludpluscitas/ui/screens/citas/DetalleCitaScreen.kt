package com.abad.saludpluscitas.ui.screens.citas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.saludpluscitas.data.repository.Repositorio
import com.abad.saludpluscitas.ui.components.BarraSuperior

@Composable
fun DetalleCitaScreen(
    citaId: Int,
    onVolver: () -> Unit
) {
    val cita = Repositorio.citas.find { it.id == citaId }
    val medico = cita?.let {
        Repositorio.obtenerMedicoPorId(it.medicoId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        BarraSuperior(
            titulo = "Detalle de cita",
            onVolver = onVolver
        )

        Column(modifier = Modifier.padding(16.dp)) {
            if (cita == null) {
                Text("No se encontró la cita.")
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = medico?.nombre ?: "Médico no encontrado",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Número de cita: ${cita.id}")
                        Text("Fecha: ${cita.fecha}")
                        Text("Hora: ${cita.hora}")
                        Text("Estado: ${cita.estado}")
                    }
                }
            }
        }
    }
}
