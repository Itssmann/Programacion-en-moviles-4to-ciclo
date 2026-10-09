package com.abad.saludpluscitas.ui.screens.agendamiento

import androidx.compose.foundation.clickable
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
fun EspecialidadesScreen(
    onSeleccionarEspecialidad: (Int) -> Unit,
    onVolver: () -> Unit
) {
    var busqueda by rememberSaveable {
        mutableStateOf("")
    }
    val especialidadesFiltradas = Repositorio.buscarEspecialidades(busqueda)
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BarraSuperior(
            titulo = "Especialidades médicas",
            onVolver = onVolver
        )

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = { Text("Buscar especialidad") },
            placeholder = { Text("Ejemplo: Cardiología") },
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
            items(especialidadesFiltradas) { especialidad ->
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
                        Text(
                            text = especialidad.nombre,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = especialidad.descripcion
                        )
                    }
                }
            }
        }
    }
}