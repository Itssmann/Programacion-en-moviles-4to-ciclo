package com.abad.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.data.local.entity.Mascota
import com.abad.huellitasvet.ui.components.MascotaAvatar
import com.abad.huellitasvet.viewmodel.MascotaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioMascotaScreen(
    viewModel: MascotaViewModel,
    duenoId: Int,
    mascotaId: Int? = null,
    onVolver: () -> Unit
) {
    val original by remember(mascotaId) {
        if (mascotaId != null) viewModel.mascota(mascotaId)
        else kotlinx.coroutines.flow.flowOf(null)
    }.collectAsState(initial = null)

    var nombre by remember(mascotaId) { mutableStateOf("") }
    var especie by remember(mascotaId) { mutableStateOf("") }
    var raza by remember(mascotaId) { mutableStateOf("") }
    var anio by remember(mascotaId) { mutableStateOf("") }
    var peso by remember(mascotaId) { mutableStateOf("") }
    var sexo by remember(mascotaId) { mutableStateOf("") }

    val errores by viewModel.errores.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val guardado by viewModel.guardado.collectAsState()

    LaunchedEffect(mascotaId) {
        viewModel.limpiarEstado()
    }

    LaunchedEffect(original) {
        original?.mascota?.let {
            nombre = it.nombre
            especie = it.especie
            raza = it.raza
            anio = it.anioNacimiento.toString()
            peso = it.peso.toString()
            sexo = it.sexo
        }
    }

    LaunchedEffect(guardado) {
        if (guardado) onVolver()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (mascotaId == null) "Nueva mascota" else "Editar mascota") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MascotaAvatar(especie, Modifier.size(100.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                isError = errores.containsKey("nombre"),
                supportingText = { errores["nombre"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Especie", modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("PERRO", "GATO", "OTRO").forEach { opcion ->
                    FilterChip(
                        selected = especie == opcion,
                        onClick = { especie = opcion },
                        label = { Text(opcion.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
            errores["especie"]?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = raza,
                onValueChange = { raza = it },
                label = { Text("Raza") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = anio,
                onValueChange = { anio = it.filter(Char::isDigit) },
                label = { Text("Año de nacimiento") },
                isError = errores.containsKey("anio"),
                supportingText = { errores["anio"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = peso,
                onValueChange = { peso = it },
                label = { Text("Peso (kg)") },
                isError = errores.containsKey("peso"),
                supportingText = { errores["peso"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Sexo", modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("MACHO", "HEMBRA").forEach { opcion ->
                    FilterChip(
                        selected = sexo == opcion,
                        onClick = { sexo = opcion },
                        label = { Text(opcion) }
                    )
                }
            }
            errores["sexo"]?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            mensaje?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    viewModel.guardar(
                        Mascota(
                            id = original?.mascota?.id ?: 0,
                            duenoId = duenoId,
                            nombre = nombre.trim(),
                            especie = especie,
                            raza = raza.trim(),
                            anioNacimiento = anio.toIntOrNull() ?: 0,
                            peso = peso.toDoubleOrNull() ?: 0.0,
                            sexo = sexo
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar mascota")
            }

            TextButton(onClick = onVolver) {
                Text("Cancelar")
            }
        }
    }
}
