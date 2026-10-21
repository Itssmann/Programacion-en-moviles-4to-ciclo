
package com.abad.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.ui.components.MascotaAvatar
import com.abad.huellitasvet.viewmodel.MascotaViewModel
import java.time.LocalDate

@Composable
fun FichaMascotaScreen(
    mascotaId: Int,
    viewModel: MascotaViewModel,
    onVolver: () -> Unit,
    onEditar: () -> Unit,
    onPedirCita: () -> Unit
) {
    val detalle by remember(mascotaId) {
        viewModel.mascota(mascotaId)
    }.collectAsState(initial = null)

    val historial by remember(mascotaId) {
        viewModel.historial(mascotaId)
    }.collectAsState()

    val mensaje by viewModel.mensaje.collectAsState()
    var pestaña by remember { mutableIntStateOf(0) }

    val mascota = detalle?.mascota

    Scaffold(
        topBar = {
            TextButton(onClick = onVolver) {
                Text("← Volver a mascotas")
            }
        }
    ) { padding ->
        if (mascota == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MascotaAvatar(mascota.especie, Modifier.size(92.dp))
                    Column {
                        Text(
                            mascota.nombre,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text("${mascota.especie} · ${mascota.raza}")
                        Text("${LocalDate.now().year - mascota.anioNacimiento} años")
                        Text("Peso actual: ${mascota.peso} kg")
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onPedirCita) {
                        Text("Pedir cita")
                    }
                    OutlinedButton(onClick = onEditar) {
                        Text("Editar")
                    }
                }

                TabRow(selectedTabIndex = pestaña) {
                    Tab(
                        selected = pestaña == 0,
                        onClick = { pestaña = 0 },
                        text = { Text("Datos") }
                    )
                    Tab(
                        selected = pestaña == 1,
                        onClick = { pestaña = 1 },
                        text = { Text("Historial clínico") }
                    )
                }

                if (pestaña == 0) {
                    Text("Nombre: ${mascota.nombre}")
                    Text("Especie: ${mascota.especie}")
                    Text("Raza: ${mascota.raza}")
                    Text("Sexo: ${mascota.sexo}")
                    Text("Nacimiento: ${mascota.anioNacimiento}")
                    Text("Peso: ${mascota.peso} kg")

                    OutlinedButton(
                        onClick = {
                            viewModel.eliminar(mascota, onVolver)
                        }
                    ) {
                        Text("Eliminar mascota")
                    }

                    mensaje?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    if (historial.isEmpty()) {
                        Text("Aún no tiene atenciones")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(historial, key = { it.cita.id }) { item ->
                                ElevatedCard(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            "● ${item.cita.fecha} · ${item.cita.hora}",
                                            color = MaterialTheme.colorScheme.primary,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text("Diagnóstico: ${item.cita.diagnostico ?: "Sin diagnóstico"}")
                                        Text("Tratamiento: ${item.cita.tratamiento ?: "Sin tratamiento"}")
                                        Text("Peso: ${item.cita.pesoRegistrado ?: mascota.peso} kg")
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
