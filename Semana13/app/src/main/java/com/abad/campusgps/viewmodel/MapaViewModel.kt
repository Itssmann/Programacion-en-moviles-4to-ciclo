package com.abad.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.campusgps.data.LugaresRepository
import com.abad.campusgps.data.UbicacionRepository
import com.abad.campusgps.model.LugarConDistancia
import com.abad.campusgps.model.Posicion
import com.abad.campusgps.model.UbicacionUi
import com.abad.campusgps.util.distanciaMetros
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository
) : ViewModel() {

    private val _estado =
        MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)

    val estado: StateFlow<UbicacionUi> =
        _estado.asStateFlow()

    private val _lugares = MutableStateFlow(
        lugaresRepo.obtenerLugares().map {
            LugarConDistancia(it, null)
        }
    )

    val lugares: StateFlow<List<LugarConDistancia>> =
        _lugares.asStateFlow()

    private val _seleccionado = MutableStateFlow<Int?>(null)

    val seleccionado: StateFlow<Int?> =
        _seleccionado.asStateFlow()

    fun buscar() {
        _estado.value = UbicacionUi.Buscando

        viewModelScope.launch {
            ubicacionRepo.obtenerPosicion()
                .onSuccess {
                    _estado.value = UbicacionUi.Lista(it)

                    // Calcular y ordenar las distancias.
                    actualizarDistancias(it)
                }
                .onFailure {
                    _estado.value = UbicacionUi.Error(mensajeDe(it))
                }
        }
    }

    private fun actualizarDistancias(pos: Posicion) {
        _lugares.value = lugaresRepo.obtenerLugares()
            .map { lugar ->
                LugarConDistancia(
                    lugar = lugar,
                    metros = distanciaMetros(
                        desde = pos,
                        hasta = Posicion(
                            latitud = lugar.latitud,
                            longitud = lugar.longitud
                        )
                    )
                )
            }
            .sortedBy { it.metros }

        Log.d("CampusGPS", "Lugares ordenados por distancia:")

        _lugares.value.forEach { item ->
            Log.d(
                "CampusGPS",
                "${item.lugar.nombre}: ${item.metros} metros"
            )
        }
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error(
            "Sin permiso de ubicación no puedo mostrar dónde estás"
        )
    }

    // TODO Paso 7:
    // fun seleccionar(id: Int?)

    // TODO Paso 8:
    // val masCercano: StateFlow<Lugar?>

    private fun mensajeDe(e: Throwable): String =
        when (e) {
            is SecurityException ->
                "Falta el permiso de ubicación"

            else ->
                e.message ?: "No se pudo obtener la ubicación"
        }
}
