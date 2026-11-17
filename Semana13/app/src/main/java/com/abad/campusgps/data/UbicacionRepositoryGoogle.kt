package com.abad.campusgps.data

import android.annotation.SuppressLint
import android.content.Context
import com.abad.campusgps.model.Posicion
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

class UbicacionRepositoryGoogle(
    context: Context
) : UbicacionRepository {

    private val cliente = LocationServices
        .getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun obtenerPosicion(): Result<Posicion> {
        return runCatching {
            val ubicacion = cliente.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await() ?: error("No se pudo obtener la ubicación actual")

            Posicion(
                latitud = ubicacion.latitude,
                longitud = ubicacion.longitude
            )
        }
    }
}
