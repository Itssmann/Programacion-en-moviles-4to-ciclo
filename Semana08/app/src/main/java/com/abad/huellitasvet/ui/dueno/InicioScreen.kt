package com.abad.huellitasvet.ui.dueno

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.ui.components.MascotaAvatar
import com.abad.huellitasvet.viewmodel.InicioViewModel

@Composable
fun InicioScreen(
    nombreDueno: String,
    viewModel: InicioViewModel,
    onMascotas: () -> Unit,
    onAgregar: () -> Unit,
    onPedirCita: () -> Unit,
    onMisCitas: () -> Unit,
    onPerfil: () -> Unit
) {
    val mascotas by viewModel.mascotas.collectAsState()
    val proximaCita by viewModel.proximaCita.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Text("⌂") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onMascotas,
                    icon = { Text("🐾") },
                    label = { Text("Mascotas") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onMisCitas,
                    icon = { Text("▦") },
                    label = { Text("Citas") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onPerfil,
                    icon = { Text("◉") },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                "¡Hola, $nombreDueno! 👋",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text("El bienestar de tus mascotas empieza aquí.")

            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Tu próxima cita",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    if (proximaCita == null) {
                        Text("Aún no tienes citas próximas")
                    } else {
                        val cita = proximaCita!!.cita
                        Text("📅 ${cita.fecha} · ${cita.hora}")
                        Text("Estado: ${cita.estado}")
                        Text("Precio: S/ ${"%.2f".format(cita.precio)}")
                    }

                    Button(onClick = onPedirCita) {
                        Text("Pedir una cita")
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Mis mascotas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onMascotas) {
                    Text("Ver todas")
                }
            }

            if (mascotas.isEmpty()) {
                Text("Todavía no tienes mascotas registradas.")
            } else {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    mascotas.forEach { mascota ->
                        ElevatedCard(
                            modifier = Modifier.width(145.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                MascotaAvatar(especie = mascota.especie)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    mascota.nombre,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    mascota.especie,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = onAgregar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Agregar mascota")
            }
        }
    }
}
