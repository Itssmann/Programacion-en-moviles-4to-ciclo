package com.abad.campusgps.ui

import android.content.ComponentCallbacks
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
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
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapLibreMapOptions
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory.*
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point

private const val ESTILO = "https://tiles.openfreemap.org/styles/liberty"
private const val FUENTE = "ubicacion-actual"
private const val ICONO = "marcador-rojo"

private class ControlCamara {
    var movidaPorUsuario = false
}

@Suppress("UNUSED_PARAMETER") // Contrato del laboratorio; lugares y selección son pasos posteriores.
@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    modifier: Modifier = Modifier
) {
    val contexto = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val vista = remember(contexto) {
        MapLibre.getInstance(contexto)
        // TextureView se compone dentro de AndroidView, sin una superficie separada.
        MapView(contexto, MapLibreMapOptions.createFromAttributes(contexto).textureMode(true))
            .apply { onCreate(null) }
    }
    val controlCamara = remember(vista) { ControlCamara() }
    var mapa by remember(vista) { mutableStateOf<MapLibreMap?>(null) }
    var estilo by remember(vista) { mutableStateOf<Style?>(null) }
    var errorMapa by remember(vista) { mutableStateOf<String?>(null) }

    DisposableEffect(vista, lifecycle) {
        var destruido = false
        var iniciado = false
        var reanudado = false
        var mapaConListener: MapLibreMap? = null
        val movimiento = MapLibreMap.OnCameraMoveStartedListener { motivo ->
            if (motivo == MapLibreMap.OnCameraMoveStartedListener.REASON_API_GESTURE) {
                // No publicar movimientos en Compose ni mover la cámara desde este callback.
                controlCamara.movidaPorUsuario = true
            }
        }
        fun pausar() {
            if (reanudado) { vista.onPause(); reanudado = false }
        }
        fun detener() {
            pausar()
            if (iniciado) { vista.onStop(); iniciado = false }
        }
        fun destruir() {
            if (!destruido) {
                mapaConListener?.removeOnCameraMoveStartedListener(movimiento)
                mapaConListener = null
                detener()
                vista.onDestroy()
                destruido = true
            }
        }
        val fallo = MapView.OnDidFailLoadingMapListener { mensaje ->
            if (!destruido) errorMapa = "No se pudo cargar el mapa: $mensaje"
        }
        vista.addOnDidFailLoadingMapListener(fallo)
        vista.getMapAsync { disponible ->
            if (!destruido) {
                mapa = disponible
                mapaConListener = disponible
                disponible.addOnCameraMoveStartedListener(movimiento)
                disponible.cameraPosition = CameraPosition.Builder()
                    .target(LatLng(-12.0464, -77.0428)).zoom(12.0).build()
                disponible.setStyle(Style.Builder().fromUri(ESTILO)) { cargado ->
                    if (!destruido) {
                        cargado.addImage(ICONO, crearMarcadorRojo())
                        cargado.addSource(GeoJsonSource(FUENTE, FeatureCollection.fromFeatures(emptyArray())))
                        cargado.addLayer(SymbolLayer("marcador-actual", FUENTE).withProperties(
                            iconImage(ICONO), iconAnchor(Property.ICON_ANCHOR_BOTTOM),
                            iconAllowOverlap(true), iconIgnorePlacement(true),
                            textField("Estás aquí"), textFont(arrayOf("Noto Sans Regular")),
                            textSize(14f), textColor(Color.rgb(24, 45, 43)),
                            textHaloColor(Color.WHITE), textHaloWidth(2f),
                            textAnchor(Property.TEXT_ANCHOR_BOTTOM), textOffset(arrayOf(0f, -3f)),
                            textAllowOverlap(true), textIgnorePlacement(true)
                        ))
                        errorMapa = null
                        estilo = cargado
                    }
                }
            }
        }
        val observador = LifecycleEventObserver { _, evento ->
            if (!destruido) when (evento) {
                Lifecycle.Event.ON_START -> { vista.onStart(); iniciado = true }
                Lifecycle.Event.ON_RESUME -> { vista.onResume(); reanudado = true }
                Lifecycle.Event.ON_PAUSE -> pausar()
                Lifecycle.Event.ON_STOP -> detener()
                Lifecycle.Event.ON_DESTROY -> destruir()
                else -> Unit
            }
        }
        val memoria = object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) = Unit
            override fun onLowMemory() { if (!destruido) vista.onLowMemory() }
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

    LaunchedEffect(estado, estilo) {
        // Una nueva búsqueda explícita permite centrar otra vez. Un gesto posterior
        // tiene prioridad aunque la respuesta de ubicación llegue con retraso.
        if (estado == UbicacionUi.Buscando) controlCamara.movidaPorUsuario = false
        val fuente = estilo?.getSourceAs<GeoJsonSource>(FUENTE) ?: return@LaunchedEffect
        if (estado is UbicacionUi.Lista) {
            val posicion = estado.posicion
            fuente.setGeoJson(Point.fromLngLat(posicion.longitud, posicion.latitud))
            if (!controlCamara.movidaPorUsuario) {
                mapa?.cancelTransitions()
                mapa?.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(posicion.latitud, posicion.longitud), 16.0
                ), 1000)
            }
        } else {
            fuente.setGeoJson(FeatureCollection.fromFeatures(emptyArray()))
        }
    }

    Box(modifier) {
        AndroidView(factory = { vista }, modifier = Modifier.fillMaxSize())
        if (errorMapa != null) {
            Surface(
                modifier = Modifier.align(Alignment.TopCenter).padding(12.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(errorMapa.orEmpty(), Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onErrorContainer)
            }
        } else if (estilo == null) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    }
}

private fun crearMarcadorRojo(): Bitmap {
    val bitmap = Bitmap.createBitmap(36, 48, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val pintura = Paint(Paint.ANTI_ALIAS_FLAG)
    val contorno = Path().apply {
        moveTo(18f, 46f)
        cubicTo(13f, 36f, 3f, 26f, 3f, 18f)
        cubicTo(3f, -2f, 33f, -2f, 33f, 18f)
        cubicTo(33f, 26f, 23f, 36f, 18f, 46f)
        close()
    }
    pintura.color = Color.rgb(211, 47, 47)
    canvas.drawPath(contorno, pintura)
    pintura.color = Color.WHITE
    pintura.style = Paint.Style.STROKE
    pintura.strokeWidth = 2f
    canvas.drawPath(contorno, pintura)
    pintura.style = Paint.Style.FILL
    canvas.drawCircle(18f, 17f, 5f, pintura)
    return bitmap
}
