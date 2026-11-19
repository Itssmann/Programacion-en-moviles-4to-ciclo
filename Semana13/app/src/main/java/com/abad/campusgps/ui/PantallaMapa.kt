package com.abad.campusgps.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.abad.campusgps.model.UbicacionUi
import com.abad.campusgps.viewmodel.MapaViewModel

@Composable
fun PantallaMapa(viewModel: MapaViewModel) {
    val estado by viewModel.estado.collectAsState()
    val contexto = LocalContext.current

    fun tienePermisoUbicacion(): Boolean {
        val preciso = ContextCompat.checkSelfPermission(
            contexto,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val aproximado = ContextCompat.checkSelfPermission(
            contexto,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return preciso || aproximado
    }

    val lanzadorPermisos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        val permisoConcedido =
            resultado[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                resultado[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        // Antes de mostrar un rechazo, comprobar también los permisos actuales.
        if (permisoConcedido || tienePermisoUbicacion()) {
            viewModel.buscar()
        } else {
            viewModel.permisoDenegado()
        }
    }

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

        Button(
            onClick = {
                if (tienePermisoUbicacion()) {
                    viewModel.buscar()
                } else {
                    lanzadorPermisos.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            enabled = estado != UbicacionUi.Buscando
        ) {
            Text("Obtener mi ubicación")
        }

        // TODO Paso 3: mostrar el mapa con MapLibre
        // TODO Paso 6: mostrar lista de lugares
    }
}
