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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(viewModel: MapaViewModel) {

    val estado by viewModel.estado.collectAsState()
    val lugares by viewModel.lugares.collectAsState()
    val seleccionado by viewModel.seleccionado.collectAsState()

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

        if (permisoConcedido || tienePermisoUbicacion()) {
            viewModel.buscar()
        } else {
            viewModel.permisoDenegado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("CampusGPS")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // Mapa en la parte superior.
            MapaUbicacion(
                estado = estado,
                lugares = lugares,
                seleccionado = seleccionado,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            // Controles de ubicación.
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 10.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {

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

                    when (val actual = estado) {

                        UbicacionUi.Inicial -> {
                            Text(
                                "Consulta tu ubicación actual",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        UbicacionUi.Buscando -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        }

                        is UbicacionUi.Lista -> {
                            Text(
                                "Latitud: ${actual.posicion.latitud}",
                                style = MaterialTheme.typography.bodySmall
                            )

                            Text(
                                "Longitud: ${actual.posicion.longitud}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        is UbicacionUi.Error -> {
                            Text(
                                text = actual.mensaje,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Lista inferior de lugares.
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    )
                ) {

                    Text(
                        text = "Lugares cercanos",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            bottom = 6.dp
                        )
                    )

                    ListaLugares(
                        lugares = lugares,
                        seleccionado = seleccionado,
                        onSeleccionar = { id ->
                            viewModel.seleccionar(id)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
