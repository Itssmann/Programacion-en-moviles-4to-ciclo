package com.abad.huellitasvet.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.data.local.relation.CitaConDetalle

@Composable
fun CitaCard(
    detalle: CitaConDetalle,
    mostrarCancelar: Boolean = false,
    onCancelar: () -> Unit = {},
    mostrarReprogramar: Boolean = false,
    onReprogramar: () -> Unit = {}
) {
    val cita = detalle.cita

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EstadoCitaChip(cita.estado)

            Text(
                "📅 ${cita.fecha} · ${cita.hora}",
                style = MaterialTheme.typography.titleMedium
            )

            Text("Mascota: ${detalle.mascota.mascota.nombre}")
            Text("Servicio: ${detalle.servicio.nombre}")
            Text("Duración: ${detalle.servicio.duracionMin} min")
            Text("Precio: S/ ${"%.2f".format(cita.precio)}")

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (mostrarReprogramar) {
                    Button(
                        onClick = onReprogramar,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reprogramar")
                    }
                }
                if (mostrarCancelar) {
                    OutlinedButton(
                        onClick = onCancelar,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar cita")
                    }
                }
            }
        }
    }
}
