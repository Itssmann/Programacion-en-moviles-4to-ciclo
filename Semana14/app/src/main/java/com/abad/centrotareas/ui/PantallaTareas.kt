package com.abad.centrotareas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.abad.centrotareas.model.*
import com.abad.centrotareas.ui.theme.Tono
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
    val secuencial by viewModel.tiempoSecuencial.collectAsState()
    val paralelo by viewModel.tiempoParalelo.collectAsState()
    val historial by viewModel.historial.collectAsState()
    val filtrado by viewModel.historialFiltrado.collectAsState()
    val filtro by viewModel.filtroHistorial.collectAsState()
    var limitePrimos by rememberSaveable { mutableStateOf("1000") }
    val contador = remember { mutableIntStateOf(0) }
    var confirmarBorrado by rememberSaveable { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.eventosUi.collect { snackbar.showSnackbar(it, duration = SnackbarDuration.Short) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("CentroTareas", style = MaterialTheme.typography.titleLarge)
                        Text("Laboratorio de concurrencia", style = MaterialTheme.typography.bodySmall)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding).imePadding(), contentAlignment = Alignment.TopCenter) {
            LazyColumn(
                modifier = Modifier.widthIn(max = 760.dp).fillMaxSize(),
                contentPadding = PaddingValues(16.dp, 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item("introduccion") {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Explora. Ejecuta. Compara.", style = MaterialTheme.typography.headlineSmall)
                        Text("Tres formas de trabajar sin detener tu pantalla.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                item("tarea") {
                    Seccion("Tarea con progreso", "Interactúa mientras la tarea avanza", Simbolo.TAREA) {
                        // Se interpreta el mensaje existente; no se crea un estado de ejecución paralelo.
                        val tono = when {
                            mensaje == "Cancelado" -> Tono.CANCELADO
                            mensaje == "Terminó" -> Tono.EXITO
                            corriendo -> Tono.ACTIVO
                            else -> Tono.NEUTRO
                        }
                        Etiqueta(when (tono) {
                            Tono.CANCELADO -> "Cancelada"
                            Tono.EXITO -> "Completada"
                            Tono.ACTIVO -> "Ejecutando"
                            else -> "Lista"
                        }, tono)
                        Progreso(progreso, tono, animar = corriendo)
                        Text(when (tono) {
                            Tono.CANCELADO -> "Tarea detenida. Se conserva el progreso alcanzado."
                            Tono.EXITO -> "Tarea completada. Consulta su duración en el historial."
                            Tono.ACTIVO -> "La tarea está en marcha. Puedes seguir tocando el contador."
                            else -> "Inicia la tarea y observa cómo avanza en tiempo real."
                        }, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Acciones {
                            Button(onClick = viewModel::tareaLarga, enabled = !corriendo) { Text("Iniciar") }
                            OutlinedButton(onClick = viewModel::cancelar, enabled = corriendo) { Text("Cancelar") }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        FilledTonalButton(onClick = { contador.intValue++ }, modifier = Modifier.fillMaxWidth()) {
                            Text("Tócame: ${contador.intValue}")
                        }
                    }
                }
                item("primos") {
                    Seccion("Contar números primos", "Sigue el cálculo en tiempo real", Simbolo.PRIMOS) {
                        val calculando = primos is PrimosUi.Calculando
                        OutlinedTextField(
                            value = limitePrimos, onValueChange = { limitePrimos = it.filter(Char::isDigit) },
                            label = { Text("Número límite") }, supportingText = { Text("Introduce un número entero a partir de 2.") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            enabled = !calculando, singleLine = true, shape = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Acciones {
                            Button(onClick = { limitePrimos.toIntOrNull()?.let(viewModel::contar) }, enabled = !calculando && (limitePrimos.toIntOrNull() ?: 0) >= 2) { Text("Contar") }
                            OutlinedButton(onClick = viewModel::cancelarPrimos, enabled = calculando) { Text("Cancelar") }
                        }
                        ResultadoPrimos(primos)
                    }
                }
                item("cargas") {
                    Seccion("Dos cargas", "Un perfil y sus pedidos, a tu ritmo", Simbolo.CARGAS) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(Modifier.weight(1f)) {
                                Text("Simular error", style = MaterialTheme.typography.titleSmall)
                                Text(if (simularError) "Activado · prueba los reintentos" else "Desactivado · carga normal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = simularError, onCheckedChange = viewModel::cambiarSimularError,
                                modifier = Modifier.semantics { contentDescription = "Simular error" },
                                colors = SwitchDefaults.colors(
                                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    uncheckedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                        }
                        Acciones {
                            Button(onClick = viewModel::cargarUnaPorUna, enabled = carga !is CargaUi.Cargando) { Text("Una por una") }
                            OutlinedButton(onClick = viewModel::cargarALaVez, enabled = carga !is CargaUi.Cargando) { Text("A la vez") }
                        }
                        ResultadoCarga(carga, viewModel::cargarALaVez)
                    }
                }
                item("comparacion") {
                    Comparacion(secuencial, paralelo, carga is CargaUi.Error)
                }
                item("historial") {
                    Seccion("Historial de actividad", "Tus últimas 5 operaciones", Simbolo.HISTORIAL) {
                        Acciones {
                            FiltroHistorial.entries.forEach { opcion ->
                                FilterChip(
                                    selected = filtro == opcion,
                                    onClick = { viewModel.cambiarFiltroHistorial(opcion) },
                                    label = { Text(when (opcion) {
                                        FiltroHistorial.TODOS -> "Todos"
                                        FiltroHistorial.TAREAS -> "Tareas"
                                        FiltroHistorial.PRIMOS -> "Primos"
                                        FiltroHistorial.CARGAS -> "Cargas"
                                    }) }
                                )
                            }
                        }
                        if (filtrado.isEmpty()) {
                            Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icono(Simbolo.HISTORIAL)
                                    Text(if (historial.isEmpty()) "Tu actividad empieza aquí" else "Sin registros en este filtro", style = MaterialTheme.typography.titleSmall)
                                    Text(if (historial.isEmpty()) "Ejecuta una tarea para ver su resultado guardado." else "Elige otro filtro para ver las demás operaciones.", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        } else {
                            filtrado.take(5).forEach { registro -> key(registro.id) { RegistroActividad(registro) } }
                        }
                        OutlinedButton(
                            onClick = { confirmarBorrado = true },
                            enabled = historial.isNotEmpty() || secuencial != null || paralelo != null,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) { Text("Limpiar") }
                    }
                }
            }
        }
    }
    if (confirmarBorrado) {
        AlertDialog(
            onDismissRequest = { confirmarBorrado = false },
            icon = { Icono(Simbolo.HISTORIAL) }, title = { Text("¿Limpiar la actividad?") },
            text = { Text("Se borrarán todos los registros y los tiempos de comparación guardados, incluidos los de otros filtros. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { viewModel.limpiarHistorial(); confirmarBorrado = false }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Borrar") }
            },
            dismissButton = { TextButton(onClick = { confirmarBorrado = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun ResultadoPrimos(estado: PrimosUi) {
    when (estado) {
        PrimosUi.Inicial -> Aviso("Listo para calcular", "El avance y los resultados aparecerán aquí.", Tono.NEUTRO)
        is PrimosUi.Calculando -> {
            Etiqueta("Calculando", Tono.ACTIVO)
            Progreso(estado.progreso)
            MetricasPrimos(estado.procesados, estado.limite, estado.cantidad)
        }
        is PrimosUi.Listo -> {
            Etiqueta("Completado", Tono.EXITO)
            Progreso(1f, Tono.EXITO, animar = false)
            Metrica("Primos encontrados", estado.cantidad.toString(), Modifier.fillMaxWidth())
            Text("Duración: ${estado.milisegundos} ms", style = MaterialTheme.typography.bodyMedium)
        }
        is PrimosUi.Cancelado -> {
            Etiqueta("Cancelado", Tono.CANCELADO)
            Progreso(estado.progreso, Tono.CANCELADO, animar = false)
            Text("El cálculo se detuvo. Estos son los resultados parciales.", style = MaterialTheme.typography.bodyMedium)
            MetricasPrimos(estado.procesados, estado.limite, estado.cantidad)
        }
    }
}

@Composable
private fun MetricasPrimos(procesados: Int, limite: Int, cantidad: Int) {
    // En vertical para acomodar enteros grandes y fuentes ampliadas sin truncarlos.
    Metrica("Números procesados de $limite", procesados.toString(), Modifier.fillMaxWidth())
    Metrica("Primos encontrados", cantidad.toString(), Modifier.fillMaxWidth())
}

@Composable
private fun ResultadoCarga(estado: CargaUi, reintentar: () -> Unit) {
    when (estado) {
        CargaUi.Inicial -> Aviso("Elige cómo cargar", "Una por una ejecuta las cargas en secuencia. A la vez las ejecuta en paralelo.", Tono.NEUTRO)
        is CargaUi.Cargando -> {
            Etiqueta("Intento ${estado.intentoActual} de ${estado.maxIntentos}", Tono.ACTIVO)
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.secondary)
            estado.mensajeEspera?.let { Aviso("Reintento en curso", it, Tono.CANCELADO) }
                ?: Text("Cargando el perfil y los pedidos…", style = MaterialTheme.typography.bodyMedium)
        }
        is CargaUi.Exito -> {
            Etiqueta("Completado", Tono.EXITO)
            Aviso(estado.modo, "Perfil: ${estado.perfil}\nPedidos: ${estado.pedidos}", Tono.EXITO)
            Metrica("Duración total", "${estado.milisegundos} ms", Modifier.fillMaxWidth())
        }
        is CargaUi.Error -> {
            Etiqueta("Fallido", Tono.ERROR)
            Aviso("No se pudo completar la carga", estado.mensaje, Tono.ERROR)
            Button(onClick = reintentar) { Text("Reintentar") }
        }
    }
}

@Composable
private fun Comparacion(secuencial: Long?, paralelo: Long?, fallo: Boolean) {
    Seccion("Comparación de rendimiento", "Tiempos reales de las últimas cargas exitosas", Simbolo.GRAFICO) {
        val maxTime = maxOf(secuencial ?: 1L, paralelo ?: 1L, 5000L).toFloat()
        BarraTiempo("Una por una · Secuencial", secuencial, maxTime, false)
        BarraTiempo("A la vez · Paralela", paralelo, maxTime, true)
        if (secuencial != null && paralelo != null && secuencial > 0) {
            // Misma fórmula y truncamiento de la interfaz original.
            val diferencia = secuencial - paralelo
            val porcentaje = ((diferencia.toFloat() / secuencial) * 100).toInt()
            Metrica("Ahorro de la ejecución paralela", "$porcentaje%", Modifier.fillMaxWidth())
            Aviso(
                when {
                    diferencia > 0 -> "A la vez fue más rápida"
                    diferencia < 0 -> "Una por una fue más rápida"
                    else -> "Ambas tardaron lo mismo"
                },
                when {
                    diferencia > 0 -> "$diferencia ms ahorrados con la ejecución paralela."
                    diferencia < 0 -> "La ejecución paralela tardó ${-diferencia} ms más."
                    else -> "Diferencia: 0 ms."
                }, if (diferencia == 0L) Tono.NEUTRO else Tono.EXITO
            )
        } else {
            Aviso("Completa la comparación", when {
                secuencial == null && paralelo == null -> "Ejecuta ambas modalidades para comparar sus tiempos."
                secuencial == null -> "Ejecuta Una por una para completar la comparación."
                paralelo == null -> "Ejecuta A la vez para completar la comparación."
                else -> "No se puede calcular el porcentaje con un tiempo secuencial de 0 ms."
            }, Tono.NEUTRO)
        }
        if (fallo) Text("La última ejecución falló y no se incluyó en las estadísticas.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun BarraTiempo(etiqueta: String, tiempo: Long?, maximo: Float, paralela: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(etiqueta, style = MaterialTheme.typography.titleSmall)
        Text(tiempo?.let { "$it ms" } ?: "Pendiente", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        LinearProgressIndicator(
            progress = { ((tiempo ?: 0L).toFloat() / maximo).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(12.dp),
            color = if (paralela) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun RegistroActividad(item: HistorialItem) {
    val tono = when {
        item.estadoRegistro == EstadoRegistro.FALLIDO || item.esError -> Tono.ERROR
        item.estadoRegistro == EstadoRegistro.CANCELADO -> Tono.CANCELADO
        else -> Tono.EXITO
    }
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icono(when (item.tipo) {
                    TipoOperacion.TAREA_PROGRESO -> Simbolo.TAREA
                    TipoOperacion.PRIMOS -> Simbolo.PRIMOS
                    TipoOperacion.CARGA_SECUENCIAL -> Simbolo.HISTORIAL
                    TipoOperacion.CARGA_PARALELA -> Simbolo.CARGAS
                })
                Text(item.titulo, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
            }
            Etiqueta(when (tono) {
                Tono.ERROR -> "Fallido"
                Tono.CANCELADO -> "Cancelado"
                else -> "Completado"
            }, tono)
            Text(item.detalle, style = MaterialTheme.typography.bodyMedium)
            Text("${item.fechaHora}\nDuración: ${item.milisegundos} ms", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
