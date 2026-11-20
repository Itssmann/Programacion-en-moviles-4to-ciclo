package com.abad.campusgps.util

import com.abad.campusgps.model.Posicion
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Genera un polígono GeoJSON circular alrededor de una coordenada [centro]
 * con un determinado [radioMetros].
 */
fun crearPoligonoCirculo(
    centro: Posicion,
    radioMetros: Float,
    puntos: Int = 64
): Polygon {
    val rTierra = 6371000.0
    val dR = radioMetros / rTierra
    val latRad = Math.toRadians(centro.latitud)
    val lngRad = Math.toRadians(centro.longitud)

    val listaPuntos = mutableListOf<Point>()

    for (i in 0..puntos) {
        val angulo = 2 * PI * i / puntos
        val lat2 = asin(
            sin(latRad) * cos(dR) + cos(latRad) * sin(dR) * cos(angulo)
        )
        val lng2 = lngRad + atan2(
            sin(angulo) * sin(dR) * cos(latRad),
            cos(dR) - sin(latRad) * sin(lat2)
        )

        listaPuntos.add(
            Point.fromLngLat(
                Math.toDegrees(lng2),
                Math.toDegrees(lat2)
            )
        )
    }

    return Polygon.fromLngLats(listOf(listaPuntos))
}
