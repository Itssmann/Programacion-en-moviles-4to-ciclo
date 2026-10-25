package com.abad.huellitasvet.ui.dueno

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    var especie by remember(mascotaId) { mutableStateOf("PERRO") }
    var raza by remember(mascotaId) { mutableStateOf("") }
    var anio by remember(mascotaId) { mutableStateOf("") }
    var peso by remember(mascotaId) { mutableStateOf("") }
    var sexo by remember(mascotaId) { mutableStateOf("MACHO") }

    val errores by viewModel.errores.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val guardado by viewModel.guardado.collectAsState()

    LaunchedEffect(mascotaId) {
        viewModel.limpiarEstado()
    }

    LaunchedEffect(original) {
        original?.mascota?.let {
            nombre = it.nombre
            especie = it.especie.ifBlank { "PERRO" }
            raza = it.raza
            anio = it.anioNacimiento.toString()
            peso = it.peso.toString()
            sexo = it.sexo.ifBlank { "MACHO" }
        }
    }

    LaunchedEffect(guardado) {
        if (guardado) onVolver()
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
                            if (mascotaId == null) "Nueva mascota" else "Editar mascota",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Avatar with green + badge
            Box(
                modifier = Modifier.size(104.dp),
                contentAlignment = Alignment.Center
            ) {
                MascotaAvatar(especie = especie, modifier = Modifier.size(96.dp))
                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "+",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                singleLine = true,
                isError = errores.containsKey("nombre"),
                supportingText = { errores["nombre"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "Especie",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "PERRO" to "Perro",
                        "GATO" to "Gato",
                        "OTRO" to "Otro"
                    ).forEach { (opcion, label) ->
                        val seleccionado = especie == opcion
                        FilterChip(
                            selected = seleccionado,
                            onClick = { especie = opcion },
                            label = { Text(label, fontWeight = FontWeight.SemiBold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                errores["especie"]?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            OutlinedTextField(
                value = raza,
                onValueChange = { raza = it },
                label = { Text("Raza") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = anio,
                    onValueChange = { anio = it.filter(Char::isDigit) },
                    label = { Text("Año de nacimiento") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errores.containsKey("anio"),
                    supportingText = { errores["anio"]?.let { Text(it) } },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it },
                    label = { Text("Peso (kg)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errores.containsKey("peso"),
                    supportingText = { errores["peso"]?.let { Text(it) } },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "Sexo",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "MACHO" to "Macho",
                        "HEMBRA" to "Hembra"
                    ).forEach { (opcion, label) ->
                        val seleccionado = sexo == opcion
                        FilterChip(
                            selected = seleccionado,
                            onClick = { sexo = opcion },
                            label = { Text(label, fontWeight = FontWeight.SemiBold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFD1FAE5),
                                selectedLabelColor = Color(0xFF065F46),
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                errores["sexo"]?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            mensaje?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(8.dp))

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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Guardar mascota", fontWeight = FontWeight.SemiBold)
            }

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Cancelar", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
