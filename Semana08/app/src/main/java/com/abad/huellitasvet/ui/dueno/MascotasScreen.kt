package com.abad.huellitasvet.ui.dueno

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.ui.components.MascotaAvatar
import com.abad.huellitasvet.viewmodel.InicioViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MascotasScreen(
    viewModel: InicioViewModel,
    onAgregar: () -> Unit,
    onAbrirMascota: (Int) -> Unit,
    bottomBar: @Composable () -> Unit = {}
) {
    val mascotas by viewModel.mascotas.collectAsState()
    val anioActual = LocalDate.now().year

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis mascotas", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = bottomBar,
        floatingActionButton = {
            FloatingActionButton(onClick = onAgregar) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { padding ->
        if (mascotas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes mascotas registradas.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mascotas, key = { it.id }) { mascota ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAbrirMascota(mascota.id) },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            MascotaAvatar(especie = mascota.especie)

                            Column {
                                Text(
                                    mascota.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("${mascota.especie} · ${mascota.raza}")
                                Text(
                                    "${anioActual - mascota.anioNacimiento} años"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
