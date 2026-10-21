package com.abad.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.data.local.entity.Servicio
import com.abad.huellitasvet.viewmodel.ServiciosViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FormularioServicioScreen(
    viewModel: ServiciosViewModel,
    servicioId: Int? = null,
    onVolver: () -> Unit
) {
    val servicios by viewModel.servicios.collectAsState()
    val original = servicios.find { it.id == servicioId }

    var nombre by remember(servicioId) { mutableStateOf("") }
    var descripcion by remember(servicioId) { mutableStateOf("") }
    var duracion by remember(servicioId) { mutableIntStateOf(30) }
    var precio by remember(servicioId) { mutableStateOf("") }
    var icono by remember(servicioId) { mutableStateOf("🐾") }
    var activo by remember(servicioId) { mutableStateOf(true) }

    val mensaje by viewModel.mensaje.collectAsState()
    val guardado by viewModel.guardado.collectAsState()

    LaunchedEffect(servicioId) {
        viewModel.limpiarEstado()
    }

    LaunchedEffect(original) {
        original?.let {
            nombre = it.nombre
            descripcion = it.descripcion
            duracion = it.duracionMin
            precio = it.precio.toString()
            icono = it.icono
            activo = it.activo
        }
    }

    LaunchedEffect(guardado) {
        if (guardado) onVolver()
    }

    Scaffold(
        topBar = {
            TextButton(onClick = onVolver) {
                Text("← Volver")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                if (servicioId == null) "Nuevo servicio" else "Editar servicio",
                style = MaterialTheme.typography.headlineMedium
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del servicio") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Duración")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(15, 30, 45, 60).forEach { minutos ->
                    FilterChip(
                        selected = duracion == minutos,
                        onClick = { duracion = minutos },
                        label = { Text("$minutos min") }
                    )
                }
            }

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio (S/)") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Ícono")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("🐾", "💉", "🩺", "✂️", "🛁").forEach { opcion ->
                    FilterChip(
                        selected = icono == opcion,
                        onClick = { icono = opcion },
                        label = { Text(opcion) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Servicio activo")
                Switch(
                    checked = activo,
                    onCheckedChange = { activo = it }
                )
            }

            mensaje?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    viewModel.guardar(
                        Servicio(
                            id = original?.id ?: 0,
                            nombre = nombre.trim(),
                            descripcion = descripcion.trim(),
                            duracionMin = duracion,
                            precio = precio.toDoubleOrNull() ?: 0.0,
                            activo = activo,
                            icono = icono
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar servicio")
            }
        }
    }
}

