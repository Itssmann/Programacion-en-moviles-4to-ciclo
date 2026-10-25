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
import com.abad.huellitasvet.ui.components.MascotaAvatar
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
            val edad = LocalDate.now().year - mascota.anioNacimiento
            val especieRaza = "${mascota.especie.lowercase().replaceFirstChar { it.uppercase() }} · ${mascota.raza}"
            val sexoPeso = "$edad años · ${mascota.sexo.lowercase().replaceFirstChar { it.uppercase() }} · ${mascota.peso} kg"

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MascotaAvatar(
                            especie = mascota.especie,
                            modifier = Modifier.size(72.dp)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = mascota.nombre,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = especieRaza,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = sexoPeso,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Buttons row ("Pedir cita" & "Editar")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onPedirCita,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Pedir cita", fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = onEditar,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Editar", fontWeight = FontWeight.SemiBold)
                    }
                }

                // Segmented control for tabs
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { pestaña = 0 },
                            shape = RoundedCornerShape(20.dp),
                            color = if (pestaña == 0) MaterialTheme.colorScheme.surface else Color.Transparent
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "Datos",
                                    color = if (pestaña == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { pestaña = 1 },
                            shape = RoundedCornerShape(20.dp),
                            color = if (pestaña == 1) MaterialTheme.colorScheme.surface else Color.Transparent
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "Historial clínico",
                                    color = if (pestaña == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                if (pestaña == 0) {
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
                                Text("Nacimiento: ${mascota.anioNacimiento}", style = MaterialTheme.typography.bodyMedium)
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
                    if (historial.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Aún no tiene atenciones registradas",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 20.dp)
                        ) {
                            items(historial, key = { it.cita.id }) { item ->
                                val fechaFormateada = runCatching {
                                    LocalDate.parse(item.cita.fecha)
                                        .format(DateTimeFormatter.ofPattern("dd MMM", Locale.forLanguageTag("es")))
                                }.getOrDefault(item.cita.fecha)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Timeline indicator
                                    Surface(
                                        modifier = Modifier.size(24.dp),
                                        shape = CircleShape,
                                        color = Color(0xFF059669)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                "✓",
                                                color = Color.White,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    // History card
                                    ElevatedCard(
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.elevatedCardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = item.servicio.nombre,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = fechaFormateada,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color(0xFF059669),
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }

                                            if (!item.cita.diagnostico.isNullOrBlank()) {
                                                Text(
                                                    text = item.cita.diagnostico,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("❤️", style = MaterialTheme.typography.bodySmall)
                                                Text(
                                                    text = "Peso: ${item.cita.pesoRegistrado ?: mascota.peso} kg",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium
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
}
