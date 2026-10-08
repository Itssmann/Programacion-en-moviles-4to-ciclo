package com.abad.saludpluscitas.ui.screens.agendamiento

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.saludpluscitas.data.repository.Repositorio
import com.abad.saludpluscitas.ui.components.BarraSuperior
import com.abad.saludpluscitas.ui.components.BotonSaludPlus

@Composable
fun ConfirmarCitaScreen(
    medicoId: Int,
    fecha: String,
    hora: String,
    onConfirmar: () -> Unit,
    onVolver: () -> Unit
) {
    val medico = Repositorio.obtenerMedicoPorId(medicoId)

    Column(modifier = Modifier.fillMaxSize()) {
        BarraSuperior(
            titulo = "Confirmar cita",
            onVolver = onVolver
        )

        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Resumen de tu cita",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Médico: ${medico?.nombre ?: "No encontrado"}")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Fecha: $fecha")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Hora: $hora")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            BotonSaludPlus(
                texto = "Confirmar cita",
                onClick = onConfirmar
            )
        }
    }
}