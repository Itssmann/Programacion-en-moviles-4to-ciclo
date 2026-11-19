package com.abad.campusgps.data

import android.annotation.SuppressLint
import android.content.Context
import com.abad.campusgps.model.Posicion
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class UbicacionNoDisponibleException :
    Exception("Activa el GPS e intenta de nuevo")

class UbicacionRepositoryGoogle(
    context: Context
) : UbicacionRepository {

    private val contexto = context.applicationContext

    private val cliente = LocationServices
        .getFusedLocationProviderClient(contexto)

    override fun playServicesDisponible(): Boolean {
        return GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(contexto) ==
                ConnectionResult.SUCCESS
    }

    @SuppressLint("MissingPermission")
    override suspend fun obtenerPosicion(): Result<Posicion> {
        return try {
            val ubicacion = cliente.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await() ?: throw UbicacionNoDisponibleException()

            Result.success(
                Posicion(
                    latitud = ubicacion.latitude,
                    longitud = ubicacion.longitude
                )
            )

        } catch (e: CancellationException) {
            throw e

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
