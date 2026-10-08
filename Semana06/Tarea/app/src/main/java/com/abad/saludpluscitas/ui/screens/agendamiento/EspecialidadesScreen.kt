package com.abad.saludpluscitas.ui.screens.agendamiento

import androidx.compose.foundation.clickable
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
fun EspecialidadesScreen(
    onSeleccionarEspecialidad: (Int) -> Unit,
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BarraSuperior(
            titulo = "Especialidades médicas",
            onVolver = onVolver
        )

        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(Repositorio.especialidades) { especialidad ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSeleccionarEspecialidad(especialidad.id)
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(especialidad.nombre)
                        Text(especialidad.descripcion)
                    }
                }
            }
        }
    }
}