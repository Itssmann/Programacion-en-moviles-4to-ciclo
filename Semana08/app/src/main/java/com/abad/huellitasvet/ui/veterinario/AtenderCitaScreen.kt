package com.abad.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.ui.components.MascotaAvatar
import com.abad.huellitasvet.viewmodel.AtenderCitaViewModel

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
            TextButton(onClick = onVolver) {
                Text("← Volver a agenda")
            }
        }
    ) { padding ->
        val cita = detalle

        if (cita == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "Atender cita",
                    style = MaterialTheme.typography.headlineMedium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MascotaAvatar(cita.mascota.mascota.especie)
                    Column {
                        Text(
                            cita.mascota.mascota.nombre,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text("Dueño: ${cita.mascota.dueno.nombre}")
                        Text("Servicio: ${cita.servicio.nombre}")
                        Text("Fecha: ${cita.cita.fecha} · ${cita.cita.hora}")
                    }
                }

                HorizontalDivider()

                Text("Peso anterior: ${cita.mascota.mascota.peso} kg")

                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it },
                    label = { Text("Peso actual (kg)") },
                    isError = errores.containsKey("peso"),
                    supportingText = {
                        errores["peso"]?.let { Text(it) }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    "Se actualizará el peso de ${cita.mascota.mascota.nombre}."
                )

                OutlinedTextField(
                    value = diagnostico,
                    onValueChange = { diagnostico = it },
                    label = { Text("Diagnóstico") },
                    minLines = 3,
                    isError = errores.containsKey("diagnostico"),
                    supportingText = {
                        errores["diagnostico"]?.let { Text(it) }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tratamiento,
                    onValueChange = { tratamiento = it },
                    label = { Text("Tratamiento") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (guardando) "Guardando..."
                        else "Registrar atención"
                    )
                }

                OutlinedButton(
                    onClick = { confirmarCancelacion = true },
                    enabled = atendible && !guardando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar cita")
                }
            }
        }
    }
}
