package com.abad.huellitasvet.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.data.local.relation.CitaConDetalle

@Composable
fun HistorialItem(
    detalle: CitaConDetalle
) {
    val cita = detalle.cita

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                "${cita.fecha} · ${cita.hora}",
                style = MaterialTheme.typography.titleMedium
            )
            Text("Servicio: ${detalle.servicio.nombre}")
            EstadoCitaChip(cita.estado)

            cita.diagnostico?.let {
                Text("Diagnóstico: $it")
            }

            cita.tratamiento?.let {
                Text("Tratamiento: $it")
            }

            cita.pesoRegistrado?.let {
                Text("Peso registrado: $it kg")
            }
        }
    }
}

