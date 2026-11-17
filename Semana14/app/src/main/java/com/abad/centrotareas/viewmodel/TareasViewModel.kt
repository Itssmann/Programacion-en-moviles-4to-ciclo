package com.abad.centrotareas.viewmodel

import androidx.lifecycle.ViewModel
import com.abad.centrotareas.data.TareasRepository
import com.abad.centrotareas.model.CargaUi
import com.abad.centrotareas.model.PrimosUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TareasViewModel(
    private val repo: TareasRepository
) : ViewModel() {

    // Tarea con progreso: pasos 1 al 4
    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    fun tareaLargaMala() {
        _mensaje.value = "Trabajando..."

        Thread.sleep(4000)

        _mensaje.value = "Terminó"
    }

    fun tareaLarga() {
        viewModelScope.launch {
            _mensaje.value = "Trabajando..."

            Log.d("HILO", "Antes: ${Thread.currentThread().name}")

            withContext(Dispatchers.Default) {
                Log.d("HILO", "Dentro: ${Thread.currentThread().name}")
                delay(4000)
            }

            Log.d("HILO", "Despues: ${Thread.currentThread().name}")

            _mensaje.value = "Terminó"
        }
    }

    // TODO Paso 2: tareaLarga()
    // TODO Paso 3: progreso
    // TODO Paso 4: trabajo, corriendo y cancelar()

    // Contar números primos: paso 5
    private val _primos =
        MutableStateFlow<PrimosUi>(PrimosUi.Inicial)

    val primos: StateFlow<PrimosUi> =
        _primos.asStateFlow()

    // TODO Paso 5: contar(hasta) y cancelarPrimos()

    // Dos cargas: pasos 6 y 7
    private val _carga =
        MutableStateFlow<CargaUi>(CargaUi.Inicial)

    val carga: StateFlow<CargaUi> =
        _carga.asStateFlow()

    // TODO Paso 6: cargarUnaPorUna() y cargarALaVez()
    // TODO Paso 7: simularError y manejo de errores
}
