
package com.abad.saludpluscitas.ui.screens.citas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.saludpluscitas.data.repository.Repositorio

@Composable
fun MisCitasScreen(
    onSeleccionarCita: (Int) -> Unit
) {
    val usuarioId = Repositorio.usuarioActual?.id

    val citas = if (usuarioId != null) {
        Repositorio.obtenerCitasPorUsuario(usuarioId)
    } else {
        emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mis citas médicas",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (citas.isEmpty()) {
            Text("Todavía no tienes citas registradas.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(citas) { cita ->
                    val medico = Repositorio.obtenerMedicoPorId(cita.medicoId)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSeleccionarCita(cita.id)
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = medico?.nombre ?: "Médico no encontrado",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Fecha: ${cita.fecha}")
                            Text("Hora: ${cita.hora}")
                            Text("Estado: ${cita.estado}")
                            Text("Toca para ver el detalle")
                        }
                    }
                }
            }
        }
    }
}
