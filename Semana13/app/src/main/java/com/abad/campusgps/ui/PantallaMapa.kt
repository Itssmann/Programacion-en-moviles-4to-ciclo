package com.abad.campusgps.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
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

    val colores = MaterialTheme.colorScheme
    Scaffold(
        containerColor = colores.background,
        topBar = {
            TopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("CampusGPS", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Mapa y lugares del campus",
                            style = MaterialTheme.typography.bodySmall,
                            color = colores.onPrimaryContainer
                        )
                    }
                },
                expandedHeight = 68.dp,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colores.primaryContainer,
                    titleContentColor = colores.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            val anchoAmplio = maxWidth >= 720.dp || (maxWidth >= 560.dp && maxHeight < 420.dp)
            val anchoControles = if (anchoAmplio) (maxWidth * 0.45f).coerceAtMost(440.dp) else maxWidth
            val tarjetasEnFila = anchoControles >= 280.dp * LocalDensity.current.fontScale.coerceAtLeast(1f)
            val mapa: @Composable (Modifier) -> Unit = { modifier ->
                Surface(
                    modifier = modifier,
                    shape = MaterialTheme.shapes.extraLarge,
                    color = colores.surface,
                    border = BorderStroke(1.dp, colores.outlineVariant),
                    shadowElevation = 3.dp
                ) {
                    MapaUbicacion(
                        estado = estado,
                        lugares = lugaresFiltrados,
                        seleccionado = seleccionado,
                        masCercanoId = masCercano?.id,
                        solicitudCentrar = solicitudCentrar,
                        radioMetros = radioSeleccionado,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            val controles: @Composable (Modifier) -> Unit = { modifier ->
                Column(
                    modifier = modifier.verticalScroll(rememberScrollState()).padding(bottom = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tarjetaUbicacion: @Composable (Modifier) -> Unit = { tarjeta ->
                        TarjetaInformativa(
                            "Tu ubicación",
                            colores.surface,
                            colores.onSurface,
                            modifier = tarjeta
                        ) {
                            when (val actual = estado) {
                                UbicacionUi.Inicial -> Text("Consulta tu ubicación actual")
                                UbicacionUi.Buscando -> Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Text("Buscando…")
                                }
                                is UbicacionUi.Lista -> {
                                    Text("Latitud: ${actual.posicion.latitud}")
                                    Text("Longitud: ${actual.posicion.longitud}")
                                }
                                is UbicacionUi.Error -> MensajeError(actual) { viewModel.buscar() }
                            }
                        }
                    }

                    val cercano = masCercano
                    val tarjetaCercano: @Composable (Modifier) -> Unit = { tarjeta ->
                        TarjetaInformativa(
                            "Lugar más cercano",
                            colores.secondaryContainer,
                            colores.onSecondaryContainer,
                            modifier = tarjeta
                        ) {
                            if (cercano != null && distanciaMasCercano != null) {
                                Text(cercano.nombre, style = MaterialTheme.typography.labelLarge)
                                Text(formatearDistancia(distanciaMasCercano))
                            }
                        }
                    }

                    if (tarjetasEnFila) {
                        Row(
                            modifier = Modifier.height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            tarjetaUbicacion(Modifier.weight(1f).fillMaxHeight())
                            tarjetaCercano(Modifier.weight(1f).fillMaxHeight())
                        }
                    } else {
                        tarjetaUbicacion(Modifier.fillMaxWidth())
                        tarjetaCercano(Modifier.fillMaxWidth())
                    }

                    val obtenerUbicacion: @Composable (Modifier) -> Unit = { accion ->
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
                            modifier = accion.heightIn(min = 48.dp),
                            enabled = estado != UbicacionUi.Buscando
                        ) {
                            Text("Obtener mi ubicación", textAlign = TextAlign.Center)
                        }
                    }
                    val irAlCercano: @Composable (Modifier) -> Unit = { accion ->
                        OutlinedButton(
                            onClick = { viewModel.irAlMasCercano() },
                            modifier = accion.heightIn(min = 48.dp),
                            enabled = cercano != null && estado is UbicacionUi.Lista
                        ) {
                            Text("Ir al más cercano", textAlign = TextAlign.Center)
                        }
                    }
                    if (tarjetasEnFila) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            obtenerUbicacion(Modifier.weight(1f))
                            irAlCercano(Modifier.weight(1f))
                        }
                    } else {
                        obtenerUbicacion(Modifier.fillMaxWidth())
                        irAlCercano(Modifier.fillMaxWidth())
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Categoría", style = MaterialTheme.typography.labelLarge)
                        FiltroCategorias(
                            categoriaSeleccionada = categoriaSeleccionada,
                            onSeleccionarCategoria = { viewModel.seleccionarCategoria(it) }
                        )
                        Text("Radio de cercanía", style = MaterialTheme.typography.labelLarge)
                        FiltroRadio(
                            radioSeleccionado = radioSeleccionado,
                            onSeleccionarRadio = { viewModel.seleccionarRadio(it) }
                        )
                    }

                    Text("Lugares cercanos", style = MaterialTheme.typography.titleMedium)
                    ListaLugares(
                        lugares = lugaresFiltrados,
                        seleccionado = seleccionado,
                        onSeleccionar = { viewModel.seleccionar(it) }
                    )
                }
            }

            if (anchoAmplio) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    mapa(Modifier.weight(1f).fillMaxHeight())
                    controles(Modifier.width(anchoControles).fillMaxHeight())
                }
            } else {
                val alturaMapa = (maxHeight * 0.42f).coerceAtMost(280.dp)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    mapa(Modifier.fillMaxWidth().height(alturaMapa))
                    controles(Modifier.fillMaxWidth().weight(1f))
                }
            }
        }
    }
}

@Composable
private fun TarjetaInformativa(
    titulo: String,
    fondo: Color,
    contenido: Color,
    modifier: Modifier = Modifier,
    cuerpo: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = fondo, contentColor = contenido)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(titulo, style = MaterialTheme.typography.labelLarge)
            ProvideTextStyle(MaterialTheme.typography.bodySmall) {
                cuerpo()
            }
        }
    }
}
