package com.abad.centrotareas.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.abad.centrotareas.model.CargaUi
import com.abad.centrotareas.model.PrimosUi
import com.abad.centrotareas.ui.theme.ColorBorde
import com.abad.centrotareas.ui.theme.ColorTarjeta
import com.abad.centrotareas.ui.theme.FondoGeneral
import com.abad.centrotareas.ui.theme.RojoErrorFondo
import com.abad.centrotareas.ui.theme.RojoErrorTexto
import com.abad.centrotareas.ui.theme.VerdeExitoFondo
import com.abad.centrotareas.ui.theme.VerdeExitoTexto
import com.abad.centrotareas.ui.theme.VerdePetroleo
import com.abad.centrotareas.ui.theme.VerdeSecundario
import com.abad.centrotareas.viewmodel.TareasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(viewModel: TareasViewModel) {

    val mensaje by viewModel.mensaje.collectAsState()
    val progreso by viewModel.progreso.collectAsState()
    val corriendo by viewModel.corriendo.collectAsState()
    val primos by viewModel.primos.collectAsState()
    val carga by viewModel.carga.collectAsState()
    val simularError by viewModel.simularError.collectAsState()
    var limitePrimos by remember { mutableStateOf("1000") }
    val calculando = primos is PrimosUi.Calculando
    val contador = remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "CentroTareas",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdePetroleo
                )
            )
        },
        containerColor = FondoGeneral
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 1. Tarea con progreso
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ColorTarjeta),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ColorBorde)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Tarea con progreso",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = VerdePetroleo,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(Modifier.height(12.dp))

                        // Botón «Tócame: X» centrado y en la parte superior
                        OutlinedButton(
                            onClick = {
                                contador.intValue++
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = VerdePetroleo),
                            border = BorderStroke(1.dp, ColorBorde)
                        ) {
                            Text("Tócame: ${contador.intValue}")
                        }

                        Spacer(Modifier.height(12.dp))

                        // Botones Iniciar y Cancelar debajo
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    viewModel.tareaLarga()
                                },
                                enabled = !corriendo,
                                colors = ButtonDefaults.buttonColors(containerColor = VerdePetroleo)
                            ) {
                                Text("Iniciar")
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.cancelar()
                                },
                                enabled = corriendo,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = VerdePetroleo),
                                border = BorderStroke(1.dp, ColorBorde)
                            ) {
                                Text("Cancelar")
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Estado: $mensaje",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LinearProgressIndicator(
                                progress = { progreso },
                                modifier = Modifier.weight(1f),
                                color = VerdeSecundario
                            )
                            Text(
                                "${(progreso * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 2. Contar números primos
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ColorTarjeta),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ColorBorde)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Contar números primos",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = VerdePetroleo
                        )

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
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
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
                                        (limitePrimos.toIntOrNull() ?: 0) >= 2,
                                colors = ButtonDefaults.buttonColors(containerColor = VerdePetroleo)
                            ) {
                                Text("Contar")
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.cancelarPrimos()
                                },
                                enabled = calculando,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = VerdePetroleo),
                                border = BorderStroke(1.dp, ColorBorde)
                            ) {
                                Text("Cancelar")
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        when (val estado = primos) {
                            PrimosUi.Inicial -> Text(
                                "Resultado pendiente",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )

                            is PrimosUi.Calculando -> {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = VerdeSecundario,
                                            strokeWidth = 2.5.dp
                                        )
                                        Text(
                                            "Calculando: ${estado.procesados} / ${estado.limite} (${(estado.progreso * 100).toInt()}%)",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    LinearProgressIndicator(
                                        progress = { estado.progreso },
                                        modifier = Modifier.fillMaxWidth(),
                                        color = VerdeSecundario
                                    )

                                    Text(
                                        "Primos encontrados: ${estado.cantidad}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = VerdePetroleo,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            is PrimosUi.Listo -> {
                                Surface(
                                    color = VerdeExitoFondo,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text(
                                            "Resultado: ${estado.cantidad} primos",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = VerdeExitoTexto
                                        )
                                        Text(
                                            "Tiempo: ${estado.milisegundos} ms",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = VerdeExitoTexto
                                        )
                                    }
                                }
                            }

                            PrimosUi.Cancelado -> {
                                Surface(
                                    color = Color(0xFFFFF3E0),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFFCC80)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "Cálculo cancelado",
                                        modifier = Modifier.padding(12.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFE65100),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Dos cargas
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ColorTarjeta),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ColorBorde)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Dos cargas",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = VerdePetroleo
                        )

                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Simular error",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )

                            Switch(
                                checked = simularError,
                                onCheckedChange = {
                                    viewModel.cambiarSimularError(it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = VerdeSecundario
                                )
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.cargarUnaPorUna() },
                                enabled = carga != CargaUi.Cargando,
                                colors = ButtonDefaults.buttonColors(containerColor = VerdePetroleo)
                            ) {
                                Text("Una por una")
                            }

                            OutlinedButton(
                                onClick = { viewModel.cargarALaVez() },
                                enabled = carga != CargaUi.Cargando,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = VerdePetroleo),
                                border = BorderStroke(1.dp, ColorBorde)
                            ) {
                                Text("A la vez")
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        when (val estado = carga) {
                            CargaUi.Inicial -> {
                                Text(
                                    "Resultados pendientes",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }

                            CargaUi.Cargando -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = VerdeSecundario,
                                        strokeWidth = 2.5.dp
                                    )
                                    Text(
                                        "Cargando datos...",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            is CargaUi.Exito -> {
                                Surface(
                                    color = VerdeExitoFondo,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Text(
                                            "Modo: ${estado.modo}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = VerdeExitoTexto
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            "Perfil: ${estado.perfil}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = VerdeExitoTexto
                                        )
                                        Text(
                                            "Pedidos: ${estado.pedidos}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = VerdeExitoTexto
                                        )
                                        Text(
                                            "Tiempo: ${estado.milisegundos} ms",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = VerdeExitoTexto,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            is CargaUi.Error -> {
                                Surface(
                                    color = RojoErrorFondo,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFEF9A9A)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Text(
                                            estado.mensaje,
                                            color = RojoErrorTexto,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(Modifier.height(8.dp))

                                        Button(
                                            onClick = {
                                                viewModel.cargarALaVez()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = RojoErrorTexto)
                                        ) {
                                            Text("Reintentar", color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}
