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
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

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
            _primos.value = PrimosUi.Calculando(procesados = 0, limite = hasta, cantidad = 0, progreso = 0f)

            val inicio = System.currentTimeMillis()
            var ultimaCantidad = 0

            try {
                repo.contarPrimosFlow(hasta).collect { progress ->
                    ultimaCantidad = progress.cantidadPrimos
                    _primos.value = PrimosUi.Calculando(
                        procesados = progress.procesados,
                        limite = progress.limite,
                        cantidad = progress.cantidadPrimos,
                        progreso = progress.porcentaje
                    )
                }

                val tiempo = System.currentTimeMillis() - inicio

                _primos.value = PrimosUi.Listo(
                    cantidad = ultimaCantidad,
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

    private val _simularError = MutableStateFlow(false)

    val simularError: StateFlow<Boolean> =
        _simularError.asStateFlow()

    fun cambiarSimularError(valor: Boolean) {
        _simularError.value = valor
    }


    fun cargarUnaPorUna() {
        ejecutarCarga(paralela = false)
    }

    fun cargarALaVez() {
        ejecutarCarga(paralela = true)
    }
    private fun ejecutarCarga(paralela: Boolean) {
        if (_carga.value == CargaUi.Cargando) return

        viewModelScope.launch {
            _carga.value = CargaUi.Cargando
            val inicio = System.currentTimeMillis()

            try {
                val (perfil, pedidos) = if (paralela) {
                    coroutineScope {
                        val perfilAsync = async {
                            repo.cargarPerfil()
                        }

                        val pedidosAsync = async {
                            repo.cargarPedidos(_simularError.value)
                        }

                        Pair(
                            perfilAsync.await(),
                            pedidosAsync.await()
                        )
                    }
                } else {
                    val perfil = repo.cargarPerfil()
                    val pedidos = repo.cargarPedidos(_simularError.value)

                    Pair(perfil, pedidos)
                }
                val tiempo = System.currentTimeMillis() - inicio

                _carga.value = CargaUi.Exito(
                    modo = if (paralela) "A la vez" else "Una por una",
                    perfil = perfil,
                    pedidos = pedidos.size,
                    milisegundos = tiempo
                )
            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _carga.value = CargaUi.Error(
                    "No se pudo cargar: ${e.message ?: "Error desconocido"}"
                )
            }
        }
    }
}
