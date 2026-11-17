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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.abad.centrotareas.model.PrimosUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(viewModel: TareasViewModel) {

    val mensaje by viewModel.mensaje.collectAsState()
    val progreso by viewModel.progreso.collectAsState()
    val corriendo by viewModel.corriendo.collectAsState()
    val primos by viewModel.primos.collectAsState()
    var limitePrimos by remember { mutableStateOf("1000") }
    val calculando = primos is PrimosUi.Calculando
    val contador = remember { mutableIntStateOf(0) }

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

                        Button(
                            onClick = {
                                viewModel.tareaLarga()
                            },
                            enabled = !corriendo
                        ) {
                            Text("Iniciar")
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.cancelar()
                            },
                            enabled = corriendo
                        ) {
                            Text("Cancelar")
                        }


                        Spacer(Modifier.height(8.dp))

                        Text("Estado: $mensaje")

                        Spacer(Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = progreso,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("${(progreso * 100).toInt()} %")

                        Spacer(Modifier.height(12.dp))
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
                            value = limitePrimos,
                            onValueChange = {
                                limitePrimos = it.filter { c -> c.isDigit() }
                            },
                            label = { Text("Número límite") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            enabled = !calculando,
                            singleLine = true
                        )

                        Spacer(Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    limitePrimos.toIntOrNull()?.let {
                                        viewModel.contar(it)
                                    }
                                },
                                enabled = !calculando &&
                                        (limitePrimos.toIntOrNull() ?: 0) >= 2
                            ) {
                                Text("Contar")
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.cancelarPrimos()
                                },
                                enabled = calculando
                            ) {
                                Text("Cancelar")
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        when (val estado = primos) {
                            PrimosUi.Inicial -> Text("Resultado pendiente")

                            PrimosUi.Calculando -> {
                                CircularProgressIndicator()
                                Text("Calculando números primos...")
                            }

                            is PrimosUi.Listo -> {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFE8F5E9)
                                    )
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text("Resultado: ${estado.cantidad} primos")
                                        Text("Tiempo: ${estado.milisegundos} ms")
                                    }
                                }
                            }

                            PrimosUi.Cancelado -> {
                                Text("Cálculo cancelado")
                            }
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
