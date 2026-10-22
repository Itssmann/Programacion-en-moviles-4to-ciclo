package com.abad.huellitasvet.ui.veterinario

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.ui.components.MascotaAvatar
import com.abad.huellitasvet.viewmodel.AtenderCitaViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtenderCitaScreen(
    viewModel: AtenderCitaViewModel,
    onVolver: () -> Unit
) {
    val detalle by viewModel.cita.collectAsState()
    val errores by viewModel.errores.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val registrada by viewModel.registrada.collectAsState()
    val guardando by viewModel.guardando.collectAsState()

    var diagnostico by remember { mutableStateOf("") }
    var tratamiento by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var confirmarCancelacion by remember { mutableStateOf(false) }

    LaunchedEffect(detalle?.cita?.id) {
        detalle?.let {
            if (peso.isEmpty()) {
                peso = it.mascota.mascota.peso.toString()
            }
        }
    }

    LaunchedEffect(registrada) {
        if (registrada) onVolver()
    }

    if (confirmarCancelacion) {
        AlertDialog(
            onDismissRequest = { confirmarCancelacion = false },
            title = { Text("Cancelar cita") },
            text = { Text("¿Confirmas la cancelación de esta cita?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.cancelar()
                    confirmarCancelacion = false
                }) {
                    Text("Sí, cancelar")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarCancelacion = false }) {
                    Text("Volver")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Atender cita",
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
        }
    ) { padding ->
        val cita = detalle

        if (cita == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val mascota = cita.mascota.mascota
            val dueno = cita.mascota.dueno
            val servicio = cita.servicio
            val edad = LocalDate.now().year - mascota.anioNacimiento
            val especieRaza = "${mascota.especie.lowercase().replaceFirstChar { it.uppercase() }} · ${mascota.raza}"
            val sexoEdad = "$edad años · ${mascota.sexo.lowercase().replaceFirstChar { it.uppercase() }} · Dueño: ${dueno.nombre}"

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Pet info card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MascotaAvatar(mascota.especie, Modifier.size(52.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "${mascota.nombre} · $especieRaza",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = sexoEdad,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "🩺 ${servicio.nombre} · ${cita.cita.fecha} · ${cita.cita.hora}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    "Registro clínico",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Weights side-by-side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = peso,
                        onValueChange = { peso = it },
                        label = { Text("Peso (kg)") },
                        isError = errores.containsKey("peso"),
                        supportingText = {
                            errores["peso"]?.let { Text(it) }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ElevatedCard(
                        modifier = Modifier.weight(1f).height(64.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = Color(0xFFDBEAFE)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Peso anterior",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF1E40AF)
                            )
                            Text(
                                "${mascota.peso} kg",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D4ED8)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = diagnostico,
                    onValueChange = { diagnostico = it },
                    label = { Text("Diagnóstico") },
                    minLines = 2,
                    isError = errores.containsKey("diagnostico"),
                    supportingText = {
                        errores["diagnostico"]?.let { Text(it) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = tratamiento,
                    onValueChange = { tratamiento = it },
                    label = { Text("Tratamiento") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Weight update info card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFDBEAFE)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✓", color = Color(0xFF1D4ED8), fontWeight = FontWeight.Bold)
                        Text(
                            "Se actualizará el peso de ${mascota.nombre} a ${peso.ifEmpty { mascota.peso.toString() }} kg",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1E40AF)
                        )
                    }
                }

                mensaje?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                val atendible = cita.cita.estado in
                        listOf("PENDIENTE", "CONFIRMADA")

                Button(
                    onClick = {
                        viewModel.registrar(
                            diagnostico = diagnostico,
                            tratamiento = tratamiento,
                            peso = peso
                        )
                    },
                    enabled = atendible && !guardando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        if (guardando) "Guardando..."
                        else "Registrar atención",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = { confirmarCancelacion = true },
                    enabled = atendible && !guardando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFDC2626)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFDC2626)
                    )
                ) {
                    Text(
                        "Cancelar cita",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
