package com.abad.campusgps.ui

import android.content.ComponentCallbacks
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.abad.campusgps.model.LugarConDistancia
import com.abad.campusgps.model.UbicacionUi
import com.abad.campusgps.util.crearPoligonoCirculo
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapLibreMapOptions
import org.maplibre.android.maps.MapView
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory.*
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point

private const val ESTILO_LIBERTY =
    "https://tiles.openfreemap.org/styles/liberty"
private const val ESTILO_DARK =
    "https://tiles.openfreemap.org/styles/dark"

private const val FUENTE = "ubicacion-actual"
private const val ICONO = "marcador-rojo"

private const val FUENTE_LUGARES = "lugares-campus"
private const val ICONO_LUGAR = "marcador-azul"

private const val FUENTE_MAS_CERCANO = "lugar-mas-cercano"
private const val ICONO_MAS_CERCANO = "marcador-verde"

private const val FUENTE_RADIO = "radio-cercania"
private const val CAPA_RADIO_RELLENO = "capa-radio-relleno"
private const val CAPA_RADIO_LINEA = "capa-radio-linea"

private class ControlCamara {
    var movidaPorUsuario = false
}

@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    masCercanoId: Int?,
    solicitudCentrar: Int,
    radioMetros: Int? = null,
    modifier: Modifier = Modifier
) {
    val contexto = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val esOscuro = isSystemInDarkTheme()
    val estiloUrl = if (esOscuro) ESTILO_DARK else ESTILO_LIBERTY

    val vista = remember(contexto) {
        MapLibre.getInstance(contexto)

        MapView(
            contexto,
            MapLibreMapOptions.createFromAttributes(contexto)
                .textureMode(true)
        ).apply {
            onCreate(null)
        }
    }

    val controlCamara = remember(vista) {
        ControlCamara()
    }

    var mapa by remember(vista) {
        mutableStateOf<MapLibreMap?>(null)
    }

    var estilo by remember(vista) {
        mutableStateOf<org.maplibre.android.maps.Style?>(null)
    }

    var errorMapa by remember(vista) {
        mutableStateOf<String?>(null)
    }

    DisposableEffect(vista, lifecycle) {

        var destruido = false
        var iniciado = false
        var reanudado = false
        var mapaConListener: MapLibreMap? = null

        val movimiento =
            MapLibreMap.OnCameraMoveStartedListener { motivo ->
                if (
                    motivo ==
                    MapLibreMap.OnCameraMoveStartedListener.REASON_API_GESTURE
                ) {
                    controlCamara.movidaPorUsuario = true
                }
            }

        fun pausar() {
            if (reanudado) {
                vista.onPause()
                reanudado = false
            }
        }

        fun detener() {
            pausar()
            if (iniciado) {
                vista.onStop()
                iniciado = false
            }
        }

        fun destruir() {
            if (!destruido) {
                mapaConListener?.removeOnCameraMoveStartedListener(
                    movimiento
                )
                mapaConListener = null
                detener()
                vista.onDestroy()
                destruido = true
            }
        }

        val fallo = MapView.OnDidFailLoadingMapListener { mensaje ->
            if (!destruido) {
                errorMapa = "No se pudo cargar el mapa: $mensaje"
            }
        }

        vista.addOnDidFailLoadingMapListener(fallo)

        vista.getMapAsync { disponible ->

            if (!destruido) {

                mapa = disponible
                mapaConListener = disponible

                disponible.addOnCameraMoveStartedListener(movimiento)

                disponible.cameraPosition =
                    CameraPosition.Builder()
                        .target(LatLng(-12.0464, -77.0428))
                        .zoom(12.0)
                        .build()
            }
        }

        val observador =
            LifecycleEventObserver { _, evento ->
                if (!destruido) {
                    when (evento) {
                        Lifecycle.Event.ON_START -> {
                            vista.onStart()
                            iniciado = true
                        }

                        Lifecycle.Event.ON_RESUME -> {
                            vista.onResume()
                            reanudado = true
                        }

                        Lifecycle.Event.ON_PAUSE -> pausar()
                        Lifecycle.Event.ON_STOP -> detener()
                        Lifecycle.Event.ON_DESTROY -> destruir()
                        else -> Unit
                    }
                }
            }

        val memoria = object : ComponentCallbacks {
            override fun onConfigurationChanged(
                newConfig: Configuration
            ) = Unit

            override fun onLowMemory() {
                if (!destruido) vista.onLowMemory()
            }
        }

        contexto.applicationContext.registerComponentCallbacks(memoria)
        lifecycle.addObserver(observador)

        onDispose {
            lifecycle.removeObserver(observador)
            contexto.applicationContext.unregisterComponentCallbacks(memoria)
            vista.removeOnDidFailLoadingMapListener(fallo)
            destruir()
        }
    }

    // Cargar o actualizar el estilo del mapa (Liberty o Dark)
    LaunchedEffect(estiloUrl, mapa) {
        val map = mapa ?: return@LaunchedEffect
        estilo = null

        map.setStyle(
            org.maplibre.android.maps.Style.Builder()
                .fromUri(estiloUrl)
        ) { cargado ->
            // Registrar los tres marcadores.
            cargado.addImage(ICONO, crearMarcadorRojo())
            cargado.addImage(ICONO_LUGAR, crearMarcadorAzul())
            cargado.addImage(
                ICONO_MAS_CERCANO,
                crearMarcadorVerde()
            )

            // Fuente de ubicación actual.
            cargado.addSource(
                GeoJsonSource(
                    FUENTE,
                    FeatureCollection.fromFeatures(emptyArray())
                )
            )

            // Fuente de lugares azules.
            cargado.addSource(
                GeoJsonSource(
                    FUENTE_LUGARES,
                    FeatureCollection.fromFeatures(emptyArray())
                )
            )

            // Fuente del lugar más cercano.
            cargado.addSource(
                GeoJsonSource(
                    FUENTE_MAS_CERCANO,
                    FeatureCollection.fromFeatures(emptyArray())
                )
            )

            // Fuente del radio de cercanía.
            cargado.addSource(
                GeoJsonSource(
                    FUENTE_RADIO,
                    FeatureCollection.fromFeatures(emptyArray())
                )
            )

            // Capas para dibujar el radio de cercanía debajo de los marcadores.
            cargado.addLayer(
                FillLayer(
                    CAPA_RADIO_RELLENO,
                    FUENTE_RADIO
                ).withProperties(
                    fillColor(Color.argb(40, 33, 150, 243)),
                    fillOpacity(0.6f)
                )
            )

            cargado.addLayer(
                LineLayer(
                    CAPA_RADIO_LINEA,
                    FUENTE_RADIO
                ).withProperties(
                    lineColor(Color.rgb(25, 118, 210)),
                    lineWidth(2f)
                )
            )

            // Lugares normales: azul.
            cargado.addLayer(
                SymbolLayer(
                    "marcadores-lugares",
                    FUENTE_LUGARES
                ).withProperties(
                    iconImage(ICONO_LUGAR),
                    iconAnchor(Property.ICON_ANCHOR_BOTTOM),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true),
                    textField("{nombre}"),
                    textFont(arrayOf("Noto Sans Regular")),
                    textSize(11f),
                    textColor(Color.rgb(25, 118, 210)),
                    textHaloColor(Color.WHITE),
                    textHaloWidth(2f),
                    textAnchor(Property.TEXT_ANCHOR_BOTTOM),
                    textOffset(arrayOf(0f, -1.8f)),
                    textAllowOverlap(false),
                    textIgnorePlacement(false)
                )
            )

            // Lugar más cercano: verde.
            cargado.addLayer(
                SymbolLayer(
                    "marcador-mas-cercano",
                    FUENTE_MAS_CERCANO
                ).withProperties(
                    iconImage(ICONO_MAS_CERCANO),
                    iconAnchor(Property.ICON_ANCHOR_BOTTOM),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true),
                    textField("{nombre}"),
                    textFont(arrayOf("Noto Sans Regular")),
                    textSize(12f),
                    textColor(Color.rgb(46, 125, 50)),
                    textHaloColor(Color.WHITE),
                    textHaloWidth(2f),
                    textAnchor(Property.TEXT_ANCHOR_BOTTOM),
                    textOffset(arrayOf(0f, -1.8f)),
                    textAllowOverlap(true),
                    textIgnorePlacement(true)
                )
            )

            // Ubicación actual: rojo.
            cargado.addLayer(
                SymbolLayer(
                    "marcador-actual",
                    FUENTE
                ).withProperties(
                    iconImage(ICONO),
                    iconAnchor(Property.ICON_ANCHOR_BOTTOM),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true),
                    textField("Estás aquí"),
                    textFont(arrayOf("Noto Sans Regular")),
                    textSize(14f),
                    textColor(Color.rgb(24, 45, 43)),
                    textHaloColor(Color.WHITE),
                    textHaloWidth(2f),
                    textAnchor(Property.TEXT_ANCHOR_BOTTOM),
                    textOffset(arrayOf(0f, -3f)),
                    textAllowOverlap(true),
                    textIgnorePlacement(true)
                )
            )

            errorMapa = null
            estilo = cargado
        }
    }

    // Actualizar ubicación actual.
    LaunchedEffect(estado, estilo) {

        if (estado == UbicacionUi.Buscando) {
            controlCamara.movidaPorUsuario = false
        }

        val fuente = estilo?.getSourceAs<GeoJsonSource>(FUENTE)
            ?: return@LaunchedEffect

        if (estado is UbicacionUi.Lista) {

            val posicion = estado.posicion

            fuente.setGeoJson(
                Point.fromLngLat(
                    posicion.longitud,
                    posicion.latitud
                )
            )

            if (!controlCamara.movidaPorUsuario) {
                mapa?.cancelTransitions()
                mapa?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(posicion.latitud, posicion.longitud),
                        16.0
                    ),
                    1000
                )
            }

        } else {
            fuente.setGeoJson(
                FeatureCollection.fromFeatures(emptyArray())
            )
        }
    }

    // Actualizar polígono del radio de cercanía.
    LaunchedEffect(estado, radioMetros, estilo) {
        val fuenteRadio = estilo?.getSourceAs<GeoJsonSource>(FUENTE_RADIO)
            ?: return@LaunchedEffect

        if (estado is UbicacionUi.Lista && radioMetros != null) {
            val poligono = crearPoligonoCirculo(
                centro = estado.posicion,
                radioMetros = radioMetros.toFloat()
            )
            fuenteRadio.setGeoJson(Feature.fromGeometry(poligono))
        } else {
            fuenteRadio.setGeoJson(
                FeatureCollection.fromFeatures(emptyArray())
            )
        }
    }

    // Actualizar marcadores azules y verde.
    LaunchedEffect(lugares, masCercanoId, estilo) {

        val fuenteAzul = estilo?.getSourceAs<GeoJsonSource>(
            FUENTE_LUGARES
        ) ?: return@LaunchedEffect

        val fuenteVerde = estilo?.getSourceAs<GeoJsonSource>(
            FUENTE_MAS_CERCANO
        ) ?: return@LaunchedEffect

        fun crearFeature(item: LugarConDistancia): Feature {
            val lugar = item.lugar

            return Feature.fromGeometry(
                Point.fromLngLat(
                    lugar.longitud,
                    lugar.latitud
                )
            ).apply {
                addStringProperty("nombre", lugar.nombre)
            }
        }

        val restantes = lugares.filter {
            it.lugar.id != masCercanoId
        }

        val cercano = lugares.firstOrNull {
            it.lugar.id == masCercanoId
        }

        fuenteAzul.setGeoJson(
            FeatureCollection.fromFeatures(
                restantes.map(::crearFeature).toTypedArray()
            )
        )

        fuenteVerde.setGeoJson(
            FeatureCollection.fromFeatures(
                cercano?.let { arrayOf(crearFeature(it)) }
                    ?: emptyArray()
            )
        )
    }

    // Centrar la cámara en el lugar seleccionado.
    LaunchedEffect(
        seleccionado,
        solicitudCentrar,
        estilo,
        mapa
    ) {

        val id = seleccionado ?: return@LaunchedEffect

        val lugar = lugares.firstOrNull {
            it.lugar.id == id
        }?.lugar ?: return@LaunchedEffect

        mapa?.cancelTransitions()

        mapa?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(lugar.latitud, lugar.longitud),
                17.0
            ),
            1000
        )
    }

    Box(modifier) {

        AndroidView(
            factory = { vista },
            modifier = Modifier.fillMaxSize()
        )

        if (errorMapa != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(12.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    errorMapa.orEmpty(),
                    Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        } else if (estilo == null) {
            CircularProgressIndicator(
                Modifier.align(Alignment.Center)
            )
        }
    }
}

// Marcador rojo: ubicación actual.
private fun crearMarcadorRojo(): Bitmap {
    return crearMarcador(Color.rgb(211, 47, 47))
}

// Marcador azul: lugares del campus.
private fun crearMarcadorAzul(): Bitmap {
    return crearMarcador(Color.rgb(25, 118, 210))
}

// Marcador verde: lugar más cercano.
private fun crearMarcadorVerde(): Bitmap {
    return crearMarcador(Color.rgb(46, 160, 67))
}

// Construcción común de los marcadores.
private fun crearMarcador(color: Int): Bitmap {

    val bitmap = Bitmap.createBitmap(
        36,
        48,
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)
    val pintura = Paint(Paint.ANTI_ALIAS_FLAG)

    val contorno = Path().apply {
        moveTo(18f, 46f)
        cubicTo(13f, 36f, 3f, 26f, 3f, 18f)
        cubicTo(3f, -2f, 33f, -2f, 33f, 18f)
        cubicTo(33f, 26f, 23f, 36f, 18f, 46f)
        close()
    }

    pintura.color = color
    canvas.drawPath(contorno, pintura)

    pintura.color = Color.WHITE
    pintura.style = Paint.Style.STROKE
    pintura.strokeWidth = 2f
    canvas.drawPath(contorno, pintura)

    pintura.style = Paint.Style.FILL
    canvas.drawCircle(18f, 17f, 5f, pintura)

    return bitmap
}
