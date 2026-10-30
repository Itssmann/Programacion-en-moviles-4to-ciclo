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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.data.local.entity.Mascota
import com.abad.huellitasvet.model.MascotaEdadUtils
import com.abad.huellitasvet.ui.components.MascotaAvatar
import com.abad.huellitasvet.viewmodel.MascotaViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

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
    var tipoEspecie by remember(mascotaId) { mutableStateOf("PERRO") }
    var razaSeleccionada by remember(mascotaId) { mutableStateOf("Labrador Retriever") }
    var otraRaza by remember(mascotaId) { mutableStateOf("") }
    var especieOtro by remember(mascotaId) { mutableStateOf("Conejo") }
    var otraEspecieOtro by remember(mascotaId) { mutableStateOf("") }
    var fechaNacimiento by remember(mascotaId) { mutableStateOf(LocalDate.now().minusYears(1)) }
    var peso by remember(mascotaId) { mutableStateOf("") }
    var sexo by remember(mascotaId) { mutableStateOf("MACHO") }

    var expandedRaza by remember { mutableStateOf(false) }
    var expandedEspecieOtro by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = fechaNacimiento.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selected = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        if (!selected.isAfter(LocalDate.now())) {
                            fechaNacimiento = selected
                        }
                    }
                    showDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val razasPerros = listOf(
        "Labrador Retriever", "Golden Retriever", "Pastor Alemán", "Bulldog Francés",
        "Chihuahua", "Husky Siberiano", "Poodle", "Schnauzer", "Beagle",
        "Rottweiler", "Shih Tzu", "Mestizo", "Otra raza"
    )

    val razasGatos = listOf(
        "Persa", "Siamés", "Maine Coon", "Bengalí", "Ragdoll",
        "Sphynx", "British Shorthair", "Angora", "Mestizo", "Otra raza"
    )

    val especiesOtros = listOf(
        "Conejo", "Pato", "Cabra", "Vaca", "Gallina", "Cerdo",
        "Caballo", "Oveja", "Hámster", "Tortuga", "Loro", "Alpaca", "Otro animal"
    )

    val errores by viewModel.errores.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val guardado by viewModel.guardado.collectAsState()

    LaunchedEffect(mascotaId) {
        viewModel.limpiarEstado()
    }

    LaunchedEffect(original) {
        original?.mascota?.let {
            nombre = it.nombre
            val espDb = it.especie.uppercase()
            if (espDb == "PERRO" || espDb == "GATO") {
                tipoEspecie = espDb
                val lista = if (espDb == "PERRO") razasPerros else razasGatos
                if (it.raza in lista) {
                    razaSeleccionada = it.raza
                } else {
                    razaSeleccionada = "Otra raza"
                    otraRaza = it.raza
                }
            } else {
                tipoEspecie = "OTRO"
                if (it.especie in especiesOtros) {
                    especieOtro = it.especie
                } else {
                    especieOtro = "Otro animal"
                    otraEspecieOtro = it.especie
                }
                razaSeleccionada = it.raza
            }
            fechaNacimiento = MascotaEdadUtils.parseFechaNacimiento(it.anioNacimiento)
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
            Box(
                modifier = Modifier.size(104.dp),
                contentAlignment = Alignment.Center
            ) {
                MascotaAvatar(especie = if (tipoEspecie == "OTRO") especieOtro else tipoEspecie, modifier = Modifier.size(96.dp))
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

            // Selector principal de Tipo de Animal
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "Tipo de animal",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "PERRO" to "🐶 Perro",
                        "GATO" to "🐱 Gato",
                        "OTRO" to "🐰 Otro"
                    ).forEach { (valEsp, label) ->
                        val seleccionado = tipoEspecie == valEsp
                        Surface(
                            onClick = {
                                tipoEspecie = valEsp
                                if (valEsp == "PERRO") razaSeleccionada = "Labrador Retriever"
                                if (valEsp == "GATO") razaSeleccionada = "Persa"
                                if (valEsp == "OTRO") especieOtro = "Conejo"
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            color = if (seleccionado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    label,
                                    fontWeight = FontWeight.Bold,
                                    color = if (seleccionado) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            if (errores["especie"] != null) {
                Text(errores["especie"]!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            if (tipoEspecie == "OTRO") {
                ExposedDropdownMenuBox(
                    expanded = expandedEspecieOtro,
                    onExpandedChange = { expandedEspecieOtro = !expandedEspecieOtro },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = especieOtro,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Especie") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEspecieOtro) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedEspecieOtro,
                        onDismissRequest = { expandedEspecieOtro = false }
                    ) {
                        especiesOtros.forEach { esp ->
                            DropdownMenuItem(
                                text = { Text(esp) },
                                onClick = {
                                    especieOtro = esp
                                    expandedEspecieOtro = false
                                }
                            )
                        }
                    }
                }

                if (especieOtro == "Otro animal") {
                    OutlinedTextField(
                        value = otraEspecieOtro,
                        onValueChange = { otraEspecieOtro = it },
                        label = { Text("Especificar especie") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            if (tipoEspecie == "PERRO" || tipoEspecie == "GATO") {
                val listaRazas = if (tipoEspecie == "PERRO") razasPerros else razasGatos
                ExposedDropdownMenuBox(
                    expanded = expandedRaza,
                    onExpandedChange = { expandedRaza = !expandedRaza },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = razaSeleccionada,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Raza") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRaza) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRaza,
                        onDismissRequest = { expandedRaza = false }
                    ) {
                        listaRazas.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r) },
                                onClick = {
                                    razaSeleccionada = r
                                    expandedRaza = false
                                }
                            )
                        }
                    }
                }

                if (razaSeleccionada == "Otra raza") {
                    OutlinedTextField(
                        value = otraRaza,
                        onValueChange = { otraRaza = it },
                        label = { Text("Especificar raza") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            } else {
                OutlinedTextField(
                    value = razaSeleccionada,
                    onValueChange = { razaSeleccionada = it },
                    label = { Text("Raza o variedad (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Fecha de nacimiento interactiva (DatePicker M3) + Cálculo de edad automático
            OutlinedCard(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "Fecha de nacimiento",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = fechaNacimiento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("es"))),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Edad: ${MascotaEdadUtils.calcularEdadTexto(MascotaEdadUtils.toStoredValue(fechaNacimiento))}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            if (errores["anio"] != null) {
                Text(errores["anio"]!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = peso,
                onValueChange = { peso = it },
                label = { Text("Peso (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = errores.containsKey("peso"),
                supportingText = { errores["peso"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Sexo selector
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
                    listOf("MACHO" to "Macho", "HEMBRA" to "Hembra").forEach { (valSexo, label) ->
                        val seleccionado = sexo == valSexo
                        Surface(
                            onClick = { sexo = valSexo },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            color = if (seleccionado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    label,
                                    fontWeight = FontWeight.Bold,
                                    color = if (seleccionado) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            if (mensaje != null) {
                Text(
                    mensaje!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    val espFinal = when (tipoEspecie) {
                        "PERRO" -> "PERRO"
                        "GATO" -> "GATO"
                        else -> {
                            if (especieOtro == "Otro animal") otraEspecieOtro.trim().ifBlank { "Otro" }
                            else especieOtro
                        }
                    }
                    val razaFinal = when (tipoEspecie) {
                        "PERRO", "GATO" -> {
                            if (razaSeleccionada == "Otra raza") otraRaza.trim().ifBlank { "Mestizo" }
                            else razaSeleccionada
                        }
                        else -> razaSeleccionada.trim()
                    }

                    val mascotaObj = Mascota(
                        id = mascotaId ?: 0,
                        duenoId = duenoId,
                        nombre = nombre.trim(),
                        especie = espFinal,
                        raza = razaFinal,
                        anioNacimiento = MascotaEdadUtils.toStoredValue(fechaNacimiento),
                        peso = peso.toDoubleOrNull() ?: 5.0,
                        sexo = sexo
                    )
                    viewModel.guardar(mascotaObj)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    if (mascotaId == null) "Registrar mascota" else "Guardar cambios",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
