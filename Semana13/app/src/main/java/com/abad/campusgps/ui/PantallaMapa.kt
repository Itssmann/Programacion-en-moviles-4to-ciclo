package com.abad.campusgps.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.abad.campusgps.model.UbicacionUi
import com.abad.campusgps.util.formatearDistancia
import com.abad.campusgps.viewmodel.MapaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(viewModel: MapaViewModel) {

    val estado by viewModel.estado.collectAsState()
    val lugaresFiltrados by viewModel.lugaresFiltrados.collectAsState()
    val categoriaSeleccionada by viewModel.categoriaSeleccionada.collectAsState()
    val radioSeleccionado by viewModel.radioSeleccionado.collectAsState()
    val seleccionado by viewModel.seleccionado.collectAsState()
    val masCercano by viewModel.masCercano.collectAsState()
    val solicitudCentrar by viewModel.solicitudCentrar.collectAsState()

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

    val distanciaMasCercano = masCercano?.let { lugar ->
        lugaresFiltrados.firstOrNull { it.lugar.id == lugar.id }?.metros
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

            // Mapa superior.
            MapaUbicacion(
                estado = estado,
                lugares = lugaresFiltrados,
                seleccionado = seleccionado,
                masCercanoId = masCercano?.id,
                solicitudCentrar = solicitudCentrar,
                radioMetros = radioSeleccionado,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            // Controles inferiores.
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
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
                            MensajeError(
                                error = actual,
                                onReintentar = {
                                    viewModel.buscar()
                                }
                            )
                        }
                    }

                    // Paso 7: lugar más cercano.
                    Button(
                        onClick = {
                            viewModel.irAlMasCercano()
                        },
                        enabled = masCercano != null &&
                                estado is UbicacionUi.Lista
                    ) {
                        Text("Ir al más cercano")
                    }

                    if (masCercano != null && distanciaMasCercano != null) {
                        Text(
                            text = "Más cercano: ${masCercano!!.nombre} " +
                                    "(${formatearDistancia(distanciaMasCercano)})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Filtros por Categoría.
                    Text(
                        text = "Categoría:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    FiltroCategorias(
                        categoriaSeleccionada = categoriaSeleccionada,
                        onSeleccionarCategoria = { cat ->
                            viewModel.seleccionarCategoria(cat)
                        }
                    )

                    // Filtros por Radio de cercanía.
                    Text(
                        text = "Radio de cercanía:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    FiltroRadio(
                        radioSeleccionado = radioSeleccionado,
                        onSeleccionarRadio = { radio ->
                            viewModel.seleccionarRadio(radio)
                        }
                    )

                    Text(
                        text = "Lugares cercanos",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    ListaLugares(
                        lugares = lugaresFiltrados,
                        seleccionado = seleccionado,
                        onSeleccionar = { id ->
                            viewModel.seleccionar(id)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    )
                }
            }
        }
    }
}
