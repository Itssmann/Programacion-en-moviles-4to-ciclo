package com.abad.huellitasvet.ui.dueno

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.model.MascotaEdadUtils
import com.abad.huellitasvet.ui.components.MascotaAvatar
import com.abad.huellitasvet.ui.components.graficos.GraficoLineas
import com.abad.huellitasvet.viewmodel.MascotaViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FichaMascotaScreen(
    mascotaId: Int,
    viewModel: MascotaViewModel,
    onVolver: () -> Unit,
    onEditar: () -> Unit,
    onPedirCita: () -> Unit,
    bottomBar: @Composable () -> Unit = {}
) {
    val detalle by remember(mascotaId) {
        viewModel.mascota(mascotaId)
    }.collectAsState(initial = null)

    val historial by remember(mascotaId) {
        viewModel.historial(mascotaId)
    }.collectAsState()

    val pesosHistorial by remember(mascotaId) {
        viewModel.pesosHistorial(mascotaId)
    }.collectAsState()

    val mensaje by viewModel.mensaje.collectAsState()
    var pestaña by remember { mutableIntStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    val mascota = detalle?.mascota

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            mascota?.nombre ?: "Ficha de mascota",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    Spacer(Modifier.width(48.dp))
                }
            )
        },
        bottomBar = bottomBar,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (mascota == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val edadTexto = MascotaEdadUtils.calcularEdadTexto(mascota.anioNacimiento)
            val fechaNac = MascotaEdadUtils.parseFechaNacimiento(mascota.anioNacimiento).format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("es")))
            val especieRaza = "${mascota.especie.lowercase().replaceFirstChar { it.uppercase() }} · ${mascota.raza}"
            val sexoPeso = "$edadTexto · ${mascota.sexo.lowercase().replaceFirstChar { it.uppercase() }} · ${mascota.peso} kg"

            val puntosGrafico = pesosHistorial.map { item ->
                val fechaFormateada = runCatching {
                    LocalDate.parse(item.cita.fecha)
                        .format(DateTimeFormatter.ofPattern("dd MMM", Locale.forLanguageTag("es")))
                }.getOrDefault(item.cita.fecha)
                fechaFormateada to (item.cita.pesoRegistrado ?: mascota.peso)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main info card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MascotaAvatar(
                            especie = mascota.especie,
                            modifier = Modifier.size(72.dp)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = mascota.nombre,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = especieRaza,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = sexoPeso,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Action buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onPedirCita,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Pedir cita", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onEditar,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Editar", fontWeight = FontWeight.Bold)
                    }
                }

                // Tabs: Historial / Datos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TabButton(
                        titulo = "Historial médico",
                        seleccionado = pestaña == 0,
                        onClick = { pestaña = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    TabButton(
                        titulo = "Información",
                        seleccionado = pestaña == 1,
                        onClick = { pestaña = 1 },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (pestaña == 1) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Nombre: ${mascota.nombre}", style = MaterialTheme.typography.bodyMedium)
                                Text("Especie: ${mascota.especie}", style = MaterialTheme.typography.bodyMedium)
                                Text("Raza: ${mascota.raza}", style = MaterialTheme.typography.bodyMedium)
                                Text("Sexo: ${mascota.sexo}", style = MaterialTheme.typography.bodyMedium)
                                Text("Nacimiento: $fechaNac ($edadTexto)", style = MaterialTheme.typography.bodyMedium)
                                Text("Peso: ${mascota.peso} kg", style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.eliminar(mascota, onVolver)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Eliminar mascota", fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else {
                    if (historial.isEmpty() && pesosHistorial.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No hay historial médico registrado",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        ) {
                            if (puntosGrafico.size >= 2) {
                                item {
                                    ElevatedCard(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                "Evolución de peso (kg)",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            GraficoLineas(
                                                datos = puntosGrafico,
                                                modifier = Modifier.fillMaxWidth().height(160.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            items(historial, key = { it.cita.id }) { detalleCita ->
                                ElevatedCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            "📅 ${detalleCita.cita.fecha} · ${detalleCita.cita.hora}",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "Servicio: ${detalleCita.servicio.nombre}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        if (!detalleCita.cita.diagnostico.isNullOrBlank()) {
                                            Text(
                                                "Diagnóstico: ${detalleCita.cita.diagnostico}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        if (!detalleCita.cita.tratamiento.isNullOrBlank()) {
                                            Text(
                                                "Tratamiento: ${detalleCita.cita.tratamiento}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (detalleCita.cita.pesoRegistrado != null) {
                                            Text(
                                                "Peso registrado: ${detalleCita.cita.pesoRegistrado} kg",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold
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
}

@Composable
fun TabButton(
    titulo: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(20.dp),
        color = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (seleccionado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
