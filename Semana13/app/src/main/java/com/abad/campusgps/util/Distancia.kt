package com.abad.campusgps.util

import android.location.Location
import com.abad.campusgps.model.Posicion

fun distanciaMetros(
    desde: Posicion,
    hasta: Posicion
): Float {
    val resultado = FloatArray(1)

    Location.distanceBetween(
        desde.latitud,
        desde.longitud,
        hasta.latitud,
        hasta.longitud,
        resultado
    )

    return resultado[0]
}
