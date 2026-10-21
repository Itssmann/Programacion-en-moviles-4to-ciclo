package com.abad.huellitasvet.ui.veterinario

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.ui.components.MascotaAvatar
import com.abad.huellitasvet.viewmodel.PacientesViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PacientesScreen(
    viewModel: PacientesViewModel,
    onVolver: () -> Unit,
    onFicha: (Int) -> Unit
) {
    val pacientes by viewModel.pacientes.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()
    var especie by remember { mutableStateOf("TODOS") }

    Scaffold(
        topBar = {
            TextButton(onClick = onVolver) {
                Text("← Panel")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Pacientes",
                style = MaterialTheme.typography.headlineMedium
            )

            OutlinedTextField(
                value = busqueda,
                onValueChange = viewModel::buscar,
                label = { Text("Buscar mascota o dueño") },
                modifier = Modifier.fillMaxWidth()
            )

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("TODOS", "PERRO", "GATO", "OTRO").forEach { opcion ->
                    FilterChip(
                        selected = especie == opcion,
                        onClick = { especie = opcion },
                        label = {
                            Text(
                                when (opcion) {
                                    "TODOS" -> "Todos"
                                    "PERRO" -> "Perros"
                                    "GATO" -> "Gatos"
                                    else -> "Otros"
                                }
                            )
                        }
                    )
                }
            }

            val filtrados = pacientes.filter {
                especie == "TODOS" || it.mascota.especie == especie
            }

            if (filtrados.isEmpty()) {
                Text("No se encontraron pacientes")
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtrados, key = { it.mascota.id }) { paciente ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFicha(paciente.mascota.id) }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            MascotaAvatar(paciente.mascota.especie)
                            Column {
                                Text(
                                    paciente.mascota.nombre,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "${paciente.mascota.raza} · " +
                                            "${viewModel.edad(paciente.mascota.anioNacimiento)} años"
                                )
                                Text("Dueño: ${paciente.dueno.nombre}")
                            }
                        }
                    }
                }
            }
        }
    }
}

