package com.abad.huellitasvet.ui.dueno

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    onPerfil: () -> Unit,
    bottomBar: @Composable () -> Unit = {}
) {
    val mascotas by viewModel.mascotas.collectAsState()
    val proximaCita by viewModel.proximaCita.collectAsState()

    val primerNombre = remember(nombreDueno) {
        val limpio = if (nombreDueno == "Bienvenido" || nombreDueno.isBlank()) "Ana" else nombreDueno
        limpio.split(" ").firstOrNull() ?: limpio
    }

    Scaffold(
        bottomBar = bottomBar
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Hola, $primerNombre",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "¿Cómo están tus mascotas hoy?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    IconButton(onClick = onPerfil) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Próxima cita card (Solid green theme)
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "Próxima cita",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            if (proximaCita == null) {
                                Text(
                                    "Aún no tienes citas próximas",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                val cita = proximaCita!!.cita
                                val mascotaNombre = proximaCita!!.mascota.mascota.nombre
                                val servicioNombre = proximaCita!!.servicio.nombre

                                Text(
                                    "$mascotaNombre · $servicioNombre",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    "📅 ${cita.fecha} · ${cita.hora}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        if (proximaCita != null) {
                            MascotaAvatar(
                                especie = proximaCita!!.mascota.mascota.especie,
                                modifier = Modifier.size(56.dp)
                            )
                        }
                    }

                    if (proximaCita != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White
                        ) {
                            Text(
                                text = proximaCita!!.cita.estado,
                                color = Color(0xFF065F46),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Mis mascotas
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
                Text(
                    "Todavía no tienes mascotas registradas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    mascotas.forEach { mascota ->
                        ElevatedCard(
                            onClick = onMascotas,
                            modifier = Modifier.width(105.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                MascotaAvatar(especie = mascota.especie, modifier = Modifier.size(52.dp))
                                Text(
                                    mascota.nombre,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1
                                )
                                Text(
                                    "Ver ficha",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // + Agregar card
                    Surface(
                        onClick = onAgregar,
                        modifier = Modifier
                            .width(105.dp)
                            .height(106.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "+",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Agregar",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Servicios section
            Text(
                "Servicios",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(
                    Triple("Consulta", Icons.Default.Favorite, onPedirCita),
                    Triple("Vacunas", Icons.Default.Healing, onPedirCita),
                    Triple("Baño", Icons.Default.ContentCut, onPedirCita)
                ).forEach { (nombre, iconoVector, onClick) ->
                    ElevatedCard(
                        onClick = onClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = Color(0xFFD1FAE5)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(14.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = iconoVector,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                nombre,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46)
                            )
                        }
                    }
                }
            }

            // Recordatorio
            if (proximaCita != null) {
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = Color(0xFFFEF3C7)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(28.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                "Recordatorio",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                            Text(
                                "${proximaCita!!.mascota.mascota.nombre}: ${proximaCita!!.servicio.nombre} el ${proximaCita!!.cita.fecha} a las ${proximaCita!!.cita.hora}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFD97706)
                            )
                        }
                    }
                }
            }

            // Main "Pedir una cita" button
            Button(
                onClick = onPedirCita,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    "Pedir una cita",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
