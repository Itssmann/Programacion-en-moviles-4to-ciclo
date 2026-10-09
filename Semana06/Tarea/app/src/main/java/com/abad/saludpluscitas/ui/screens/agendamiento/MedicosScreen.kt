package com.abad.saludpluscitas.ui.screens.agendamiento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
    var busqueda by rememberSaveable {
        mutableStateOf("")
    }
    val medicos = Repositorio.obtenerMedicosPorEspecialidad(especialidadId)

    val medicosFiltrados = medicos.filter { medico ->
        medico.nombre.contains(busqueda, ignoreCase = true)
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BarraSuperior(
            titulo = "Médicos disponibles",
            onVolver = onVolver
        )
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = { Text("Buscar médico") },
            placeholder = { Text("Nombre del médico") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(medicosFiltrados) { medico ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = medico.nombre,
                            style = MaterialTheme.typography.titleMedium
                        )

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