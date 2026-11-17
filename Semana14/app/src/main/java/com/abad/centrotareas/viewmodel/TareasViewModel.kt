package com.abad.centrotareas.viewmodel

import androidx.lifecycle.ViewModel
import com.abad.centrotareas.data.TareasRepository
import com.abad.centrotareas.model.CargaUi
import com.abad.centrotareas.model.PrimosUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException

class TareasViewModel(
    private val repo: TareasRepository
) : ViewModel() {

    // Tarea con progreso: pasos 1 al 4
    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _progreso = MutableStateFlow(0f)
    val progreso: StateFlow<Float> = _progreso.asStateFlow()

    private var trabajo: Job? = null

    private val _corriendo = MutableStateFlow(false)
    val corriendo: StateFlow<Boolean> = _corriendo.asStateFlow()

    fun tareaLargaMala() {
        _mensaje.value = "Trabajando..."

        Thread.sleep(4000)

        _mensaje.value = "Terminó"
    }


    fun tareaLarga() {
        if (_corriendo.value) return

        trabajo = viewModelScope.launch {
            _corriendo.value = true
            _progreso.value = 0f
            _mensaje.value = "Trabajando..."

            try {
                for (i in 1..100) {
                    delay(40)
                    _progreso.value = i / 100f
                }

                _mensaje.value = "Terminó"

            } finally {
                _corriendo.value = false
            }
        }
    }

    fun cancelar() {
        trabajo?.cancel()
        _mensaje.value = "Cancelado"
    }
    // Contar números primos: paso 5
    private val _primos =
        MutableStateFlow<PrimosUi>(PrimosUi.Inicial)

    val primos: StateFlow<PrimosUi> =
        _primos.asStateFlow()

    private var primosJob: Job? = null

    fun contar(hasta: Int) {
        if (hasta < 2) return
        if (primosJob?.isActive == true) return

        primosJob = viewModelScope.launch {
            _primos.value = PrimosUi.Calculando

            val inicio = System.currentTimeMillis()

            try {
                val cantidad = repo.contarPrimos(hasta)

                val tiempo = System.currentTimeMillis() - inicio

                _primos.value = PrimosUi.Listo(
                    cantidad = cantidad,
                    milisegundos = tiempo
                )

            } catch (e: CancellationException) {
                _primos.value = PrimosUi.Cancelado
                throw e
            }
        }
    }
    fun cancelarPrimos() {
        primosJob?.cancel()
    }

    // Dos cargas: pasos 6 y 7
    private val _carga =
        MutableStateFlow<CargaUi>(CargaUi.Inicial)

    val carga: StateFlow<CargaUi> =
        _carga.asStateFlow()

    // TODO Paso 6: cargarUnaPorUna() y cargarALaVez()
    // TODO Paso 7: simularError y manejo de errores
}
