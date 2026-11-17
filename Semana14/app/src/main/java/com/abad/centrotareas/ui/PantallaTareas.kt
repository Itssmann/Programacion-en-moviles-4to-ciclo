package com.abad.centrotareas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.centrotareas.viewmodel.TareasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(viewModel: TareasViewModel) {

    val mensaje by viewModel.mensaje.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CentroTareas") }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Tarea con progreso",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.height(12.dp))

                        Text("Contador de toques: 0")
                        Text("Estado: $mensaje")

                        Spacer(Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { 0f },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("0 %")

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {},
                            enabled = false
                        ) {
                            Text("Iniciar")
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Contar números primos",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.height(12.dp))

                        Text("Calcula la cantidad de números primos hasta N.")

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = "1000",
                            onValueChange = {},
                            enabled = false,
                            label = { Text("Número límite") }
                        )

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {},
                            enabled = false
                        ) {
                            Text("Contar")
                        }

                        Text("Resultado pendiente")
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Dos cargas",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.height(12.dp))

                        Text("Compara las cargas secuenciales y paralelas.")

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {},
                            enabled = false
                        ) {
                            Text("Una por una")
                        }

                        OutlinedButton(
                            onClick = {},
                            enabled = false
                        ) {
                            Text("A la vez")
                        }

                        Text("Resultados pendientes")
                    }
                }
            }
        }
    }
}
