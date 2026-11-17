package com.abad.campusgps.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.campusgps.model.UbicacionUi
import com.abad.campusgps.viewmodel.MapaViewModel

@Composable
fun PantallaMapa(viewModel: MapaViewModel) {
    val estado by viewModel.estado.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "CampusGPS",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        when (val actual = estado) {
            UbicacionUi.Inicial -> {
                Text("Consulta tu ubicación actual")
            }

            UbicacionUi.Buscando -> {
                CircularProgressIndicator()
            }

            is UbicacionUi.Lista -> {
                Text("Latitud: ${actual.posicion.latitud}")
                Text("Longitud: ${actual.posicion.longitud}")
            }

            is UbicacionUi.Error -> {
                Text(
                    text = actual.mensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { viewModel.buscar() }) {
            Text("Obtener ubicación")
        }

        // TODO Paso 3: solicitar permisos en tiempo de ejecución
        // TODO Paso 4: mostrar Google Maps
        // TODO Paso 7: mostrar lista de lugares
    }
}
