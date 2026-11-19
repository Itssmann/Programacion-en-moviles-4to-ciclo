package com.abad.campusgps.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.campusgps.data.LugaresRepository
import com.abad.campusgps.data.UbicacionRepository
import com.abad.campusgps.model.Lugar
import com.abad.campusgps.model.LugarConDistancia
import com.abad.campusgps.model.Posicion
import com.abad.campusgps.model.UbicacionUi
import com.abad.campusgps.util.distanciaMetros
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    // Lugar más cercano al usuario.
    val masCercano: StateFlow<Lugar?> =
        _lugares
            .map { lista ->
                lista.firstOrNull { it.metros != null }?.lugar
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )

    // Evento para solicitar que el mapa centre el lugar.
    private val _solicitudCentrar = MutableStateFlow(0)

    val solicitudCentrar: StateFlow<Int> =
        _solicitudCentrar.asStateFlow()

    fun buscar() {
        _estado.value = UbicacionUi.Buscando

        viewModelScope.launch {
            ubicacionRepo.obtenerPosicion()
                .onSuccess {
                    _estado.value = UbicacionUi.Lista(it)
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

    fun seleccionar(id: Int?) {
        _seleccionado.value = id
    }

    // Paso 7: ir al lugar más cercano.
    fun irAlMasCercano() {
        val lugar = masCercano.value ?: return

        _seleccionado.value = lugar.id
        _solicitudCentrar.value++
    }
    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error(
            "Sin permiso de ubicación no puedo mostrar dónde estás"
        )
    }

    private fun mensajeDe(e: Throwable): String =
        when (e) {
            is SecurityException ->
                "Falta el permiso de ubicación"

            else ->
                e.message ?: "No se pudo obtener la ubicación"
        }
}
