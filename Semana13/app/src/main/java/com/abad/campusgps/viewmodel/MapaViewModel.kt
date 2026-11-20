package com.abad.campusgps.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.campusgps.data.LugaresRepository
import com.abad.campusgps.data.UbicacionNoDisponibleException
import com.abad.campusgps.data.UbicacionRepository
import com.abad.campusgps.model.Lugar
import com.abad.campusgps.model.LugarConDistancia
import com.abad.campusgps.model.Posicion
import com.abad.campusgps.model.TipoError
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

    val masCercano: StateFlow<Lugar?> =
        _lugares.map { lista ->
            lista.firstOrNull { it.metros != null }?.lugar
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    private val _solicitudCentrar = MutableStateFlow(0)

    val solicitudCentrar: StateFlow<Int> =
        _solicitudCentrar.asStateFlow()

    fun buscar() {
        viewModelScope.launch {

            if (!ubicacionRepo.playServicesDisponible()) {
                _estado.value = UbicacionUi.Error(
                    mensaje = "Este dispositivo no tiene Google Play Services.",
                    tipo = TipoError.SIN_PLAY_SERVICES
                )
                return@launch
            }

            _estado.value = UbicacionUi.Buscando

            ubicacionRepo.obtenerPosicion()
                .onSuccess { posicion ->
                    _estado.value = UbicacionUi.Lista(posicion)
                    actualizarDistancias(posicion)
                }
                .onFailure { error ->
                    val tipo = when (error) {
                        is SecurityException ->
                            TipoError.SIN_PERMISO

                        is UbicacionNoDisponibleException ->
                            TipoError.GPS_APAGADO

                        else ->
                            TipoError.OTRO
                    }

                    val mensaje = when (tipo) {
                        TipoError.SIN_PERMISO ->
                            "No tienes permiso de ubicación."

                        TipoError.GPS_APAGADO ->
                            "Activa el GPS e intenta de nuevo."

                        TipoError.SIN_PLAY_SERVICES ->
                            "Este dispositivo no tiene Google Play Services."

                        TipoError.OTRO ->
                            error.message ?: "No se pudo obtener la ubicación."
                    }

                    _estado.value = UbicacionUi.Error(
                        mensaje = mensaje,
                        tipo = tipo
                    )
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

    fun irAlMasCercano() {
        val lugar = masCercano.value ?: return

        _seleccionado.value = lugar.id
        _solicitudCentrar.value++
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error(
            mensaje = "Sin permiso de ubicación. Puedes habilitarlo desde ajustes.",
            tipo = TipoError.SIN_PERMISO
        )
    }
}
