package com.abad.centrotareas.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.abad.centrotareas.model.CargaUi
import com.abad.centrotareas.model.FiltroHistorial
import com.abad.centrotareas.model.HistorialItem
import com.abad.centrotareas.model.PrimosUi
import com.abad.centrotareas.model.TipoOperacion
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

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventosUi.collect { mensajeEvento ->
            snackbarHostState.showSnackbar(
                message = mensajeEvento,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ) { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = VerdePetroleo,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
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

                            is PrimosUi.Cancelado -> {
                                Surface(
                                    color = Color(0xFFFFF8E1),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFFE082)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Canvas(modifier = Modifier.size(22.dp)) {
                                                    val strokeWidth = size.width * 0.15f
                                                    val pad = size.width * 0.2f
                                                    val colorX = Color(0xFFE65100)

                                                    drawLine(
                                                        color = colorX,
                                                        start = Offset(pad, pad),
                                                        end = Offset(size.width - pad, size.height - pad),
                                                        strokeWidth = strokeWidth
                                                    )
                                                    drawLine(
                                                        color = colorX,
                                                        start = Offset(size.width - pad, pad),
                                                        end = Offset(pad, size.height - pad),
                                                        strokeWidth = strokeWidth
                                                    )
                                                }
                                                Text(
                                                    "Cálculo cancelado",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFE65100)
                                                )
                                            }

                                            Text(
                                                "${(estado.progreso * 100).toInt()}%",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFFE65100)
                                            )
                                        }

                                        LinearProgressIndicator(
                                            progress = { estado.progreso },
                                            modifier = Modifier.fillMaxWidth(),
                                            color = Color(0xFFF57C00),
                                            trackColor = Color(0xFFFFE0B2)
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Surface(
                                                color = Color(0xFFFFF3E0),
                                                shape = RoundedCornerShape(6.dp),
                                                border = BorderStroke(1.dp, Color(0xFFFFE0B2)),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Column(modifier = Modifier.padding(8.dp)) {
                                                    Text(
                                                        "Números procesados",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color(0xFF8D6E63)
                                                    )
                                                    Text(
                                                        "${estado.procesados} / ${estado.limite}",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF5D4037)
                                                    )
                                                }
                                            }

                                            Spacer(Modifier.width(8.dp))

                                            Surface(
                                                color = Color(0xFFFFF3E0),
                                                shape = RoundedCornerShape(6.dp),
                                                border = BorderStroke(1.dp, Color(0xFFFFE0B2)),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Column(modifier = Modifier.padding(8.dp)) {
                                                    Text(
                                                        "Primos encontrados",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color(0xFF8D6E63)
                                                    )
                                                    Text(
                                                        "${estado.cantidad}",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF5D4037)
                                                    )
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
                                modifier = Modifier.scale(1.05f),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = VerdePetroleo,
                                    checkedBorderColor = VerdePetroleo,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFE0E0E0),
                                    uncheckedBorderColor = Color(0xFF757575)
                                )
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.cargarUnaPorUna() },
                                enabled = carga !is CargaUi.Cargando,
                                colors = ButtonDefaults.buttonColors(containerColor = VerdePetroleo)
                            ) {
                                Text("Una por una")
                            }

                            OutlinedButton(
                                onClick = { viewModel.cargarALaVez() },
                                enabled = carga !is CargaUi.Cargando,
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

                            is CargaUi.Cargando -> {
                                Surface(
                                    color = Color(0xFFF5F5F5),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, ColorBorde),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(22.dp),
                                                color = VerdeSecundario,
                                                strokeWidth = 2.5.dp
                                            )
                                            Text(
                                                "Cargando datos — Intento ${estado.intentoActual} de ${estado.maxIntentos}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = VerdePetroleo
                                            )
                                        }

                                        estado.mensajeEspera?.let { msg ->
                                            Surface(
                                                color = Color(0xFFFFF3E0),
                                                shape = RoundedCornerShape(6.dp),
                                                border = BorderStroke(1.dp, Color(0xFFFFCC80))
                                            ) {
                                                Text(
                                                    msg,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color(0xFFE65100),
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
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
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFFEF9A9A)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Canvas(modifier = Modifier.size(22.dp)) {
                                                val path = Path().apply {
                                                    moveTo(size.width / 2f, 1f)
                                                    lineTo(size.width - 1f, size.height - 1f)
                                                    lineTo(1f, size.height - 1f)
                                                    close()
                                                }
                                                drawPath(path = path, color = RojoErrorTexto)

                                                drawLine(
                                                    color = Color.White,
                                                    start = Offset(size.width / 2f, size.height * 0.35f),
                                                    end = Offset(size.width / 2f, size.height * 0.65f),
                                                    strokeWidth = 3f
                                                )
                                                drawCircle(
                                                    color = Color.White,
                                                    radius = 2f,
                                                    center = Offset(size.width / 2f, size.height * 0.78f)
                                                )
                                            }

                                            Text(
                                                "Error de conexión",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = RojoErrorTexto
                                            )
                                        }

                                        Text(
                                            estado.mensaje,
                                            color = Color(0xFF5C0000),
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        Button(
                                            onClick = {
                                                viewModel.cargarALaVez()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = RojoErrorTexto),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Text("Reintentar", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Comparación de rendimiento
            item {
                val tSecuencial by viewModel.tiempoSecuencial.collectAsState()
                val tParalelo by viewModel.tiempoParalelo.collectAsState()

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ColorTarjeta),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ColorBorde)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Comparación de rendimiento",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = VerdePetroleo
                        )

                        val maxTime = maxOf(tSecuencial ?: 1L, tParalelo ?: 1L, 5000L).toFloat()

                        // Bar 1: Secuencial
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Una por una (Secuencial)",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    if (tSecuencial != null) "${tSecuencial} ms" else "Pendiente",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (tSecuencial != null) Color.DarkGray else Color.Gray
                                )
                            }
                            LinearProgressIndicator(
                                progress = { ((tSecuencial ?: 0L).toFloat() / maxTime).coerceIn(0.05f, 1f) },
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFFE53935),
                                trackColor = Color(0xFFFFEBEE)
                            )
                        }

                        // Bar 2: Paralela
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "A la vez (Paralela)",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    if (tParalelo != null) "${tParalelo} ms" else "Pendiente",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (tParalelo != null) VerdePetroleo else Color.Gray
                                )
                            }
                            LinearProgressIndicator(
                                progress = { ((tParalelo ?: 0L).toFloat() / maxTime).coerceIn(0.05f, 1f) },
                                modifier = Modifier.fillMaxWidth(),
                                color = VerdeSecundario,
                                trackColor = Color(0xFFE0F2F1)
                            )
                        }

                        // Conclusión dinámica
                        val conclusionTexto = when {
                            tSecuencial != null && tParalelo != null -> {
                                val dif = tSecuencial!! - tParalelo!!
                                val porcentaje = ((dif.toFloat() / tSecuencial!!) * 100).toInt()
                                "La ejecución paralela fue un $porcentaje% más rápida ($dif ms ahorrados)"
                            }
                            tSecuencial == null && tParalelo == null -> "Ejecuta ambas modalidades ('Una por una' y 'A la vez') para ver el análisis de eficiencia."
                            tSecuencial == null -> "Ejecuta la prueba 'Una por una' para completar la comparación."
                            else -> "Ejecuta la prueba 'A la vez' para completar la comparación."
                        }

                        Surface(
                            color = if (tSecuencial != null && tParalelo != null) VerdeExitoFondo else Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (tSecuencial != null && tParalelo != null) Color(0xFFA5D6A7) else ColorBorde),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                conclusionTexto,
                                modifier = Modifier.padding(10.dp),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (tSecuencial != null && tParalelo != null) VerdeExitoTexto else Color.DarkGray
                            )
                        }
                    }
                }
            }

            // 5. Historial de actividad
            item {
                val historialFiltrado by viewModel.historialFiltrado.collectAsState()
                val filtroActual by viewModel.filtroHistorial.collectAsState()
                var mostrarDialogoBorrar by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ColorTarjeta),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ColorBorde)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Historial de actividad",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = VerdePetroleo
                            )

                            if (historialFiltrado.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = { mostrarDialogoBorrar = true },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RojoErrorTexto),
                                    border = BorderStroke(1.dp, Color(0xFFEF9A9A)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("Limpiar", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        // FilterChips
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = filtroActual == FiltroHistorial.TODOS,
                                onClick = { viewModel.cambiarFiltroHistorial(FiltroHistorial.TODOS) },
                                label = { Text("Todos") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VerdePetroleo,
                                    selectedLabelColor = Color.White
                                )
                            )

                            FilterChip(
                                selected = filtroActual == FiltroHistorial.PRIMOS,
                                onClick = { viewModel.cambiarFiltroHistorial(FiltroHistorial.PRIMOS) },
                                label = { Text("Primos") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VerdePetroleo,
                                    selectedLabelColor = Color.White
                                )
                            )

                            FilterChip(
                                selected = filtroActual == FiltroHistorial.CARGAS,
                                onClick = { viewModel.cambiarFiltroHistorial(FiltroHistorial.CARGAS) },
                                label = { Text("Cargas") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VerdePetroleo,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }

                        if (historialFiltrado.isEmpty()) {
                            Surface(
                                color = Color(0xFFFAFAFA),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, ColorBorde),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "No hay registros guardados aún",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                historialFiltrado.take(5).forEach { item ->
                                    Surface(
                                        color = Color(0xFFFAFAFA),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, ColorBorde),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    item.titulo,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = VerdePetroleo
                                                )
                                                Text(
                                                    item.detalle,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.DarkGray
                                                )
                                                Text(
                                                    item.fechaHora,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color.Gray
                                                )
                                            }

                                            Surface(
                                                color = VerdeSecundario.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    "${item.milisegundos} ms",
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = VerdePetroleo
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (mostrarDialogoBorrar) {
                            AlertDialog(
                                onDismissRequest = { mostrarDialogoBorrar = false },
                                title = { Text("Borrar historial") },
                                text = { Text("¿Estás seguro de que deseas eliminar los registros del historial?") },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            viewModel.limpiarHistorial()
                                            mostrarDialogoBorrar = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = RojoErrorTexto)
                                    ) {
                                        Text("Borrar")
                                    }
                                },
                                dismissButton = {
                                    OutlinedButton(
                                        onClick = { mostrarDialogoBorrar = false }
                                    ) {
                                        Text("Cancelar")
                                    }
                                }
                            )
                        }
                    }
                }
            }

        }
    }
}
