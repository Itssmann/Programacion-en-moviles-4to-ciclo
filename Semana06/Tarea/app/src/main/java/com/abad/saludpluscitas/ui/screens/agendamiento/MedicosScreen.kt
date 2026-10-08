package com.abad.saludpluscitas.ui.screens.agendamiento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.saludpluscitas.data.repository.Repositorio
import com.abad.saludpluscitas.ui.components.BarraSuperior

@Composable
fun MedicosScreen(
    especialidadId: Int,
    onSeleccionarMedico: (Int) -> Unit,
    onVolver: () -> Unit
) {
    val medicos = Repositorio.obtenerMedicosPorEspecialidad(especialidadId)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BarraSuperior(
            titulo = "Médicos disponibles",
            onVolver = onVolver
        )

        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(medicos) { medico ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(medico.nombre)
                        Text("${medico.experiencia} años de experiencia")
                        Text("Calificación: ${medico.calificacion}")

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                onSeleccionarMedico(medico.id)
                            }
                        ) {
                            Text("Seleccionar médico")
                        }
                    }
                }
            }
        }
    }
}