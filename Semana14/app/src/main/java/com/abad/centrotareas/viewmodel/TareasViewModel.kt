package com.abad.centrotareas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.centrotareas.data.PrimosProgress
import com.abad.centrotareas.data.TareasRepository
import com.abad.centrotareas.data.UserPreferencesRepository
import com.abad.centrotareas.model.CargaUi
import com.abad.centrotareas.model.FiltroHistorial
import com.abad.centrotareas.model.HistorialItem
import com.abad.centrotareas.model.PrimosUi
import com.abad.centrotareas.model.TipoOperacion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class TareasViewModel(
    private val repo: TareasRepository,
    private val preferencesRepo: UserPreferencesRepository
) : ViewModel() {

    // Eventos de interfaz (Snackbar)
    private val _eventosUi = MutableSharedFlow<String>()
    val eventosUi: SharedFlow<String> = _eventosUi.asSharedFlow()

    private fun emitirEventoUi(mensaje: String) {
        viewModelScope.launch {
            _eventosUi.emit(mensaje)
        }
    }

    // Persistencia e Historial con DataStore
    val historial: StateFlow<List<HistorialItem>> = preferencesRepo.historialFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tiempoSecuencial: StateFlow<Long?> = preferencesRepo.tiempoSecuencialFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val tiempoParalelo: StateFlow<Long?> = preferencesRepo.tiempoParaleloFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _filtroHistorial = MutableStateFlow(FiltroHistorial.TODOS)
    val filtroHistorial: StateFlow<FiltroHistorial> = _filtroHistorial.asStateFlow()

    val historialFiltrado: StateFlow<List<HistorialItem>> = combine(historial, _filtroHistorial) { lista, filtro ->
        when (filtro) {
            FiltroHistorial.TODOS -> lista
            FiltroHistorial.PRIMOS -> lista.filter { it.tipo == TipoOperacion.PRIMOS }
            FiltroHistorial.CARGAS -> lista.filter { it.tipo != TipoOperacion.PRIMOS }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cambiarFiltroHistorial(filtro: FiltroHistorial) {
        _filtroHistorial.value = filtro
    }

    fun limpiarHistorial() {
        viewModelScope.launch {
            preferencesRepo.limpiarHistorial()
            emitirEventoUi("Historial de actividad borrado")
        }
    }

    private fun obtenerFechaHoraActual(): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
    }

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
                emitirEventoUi("Tarea de progreso completada")

            } finally {
                _corriendo.value = false
            }
        }
    }

    fun cancelar() {
        trabajo?.cancel()
        _mensaje.value = "Cancelado"
        emitirEventoUi("Tarea cancelada")
    }

    // Contar números primos: paso 5
    private val _primos = MutableStateFlow<PrimosUi>(PrimosUi.Inicial)
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()

    private var primosJob: Job? = null

    fun contar(hasta: Int) {
        if (hasta < 2) return
        if (primosJob?.isActive == true) return

        primosJob = viewModelScope.launch {
            _primos.value = PrimosUi.Calculando(procesados = 0, limite = hasta, cantidad = 0, progreso = 0f)

            val inicio = System.currentTimeMillis()
            var ultimoProgreso = PrimosProgress(0, hasta, 0, 0f)

            try {
                repo.contarPrimosFlow(hasta).collect { progress ->
                    ultimoProgreso = progress
                    _primos.value = PrimosUi.Calculando(
                        procesados = progress.procesados,
                        limite = progress.limite,
                        cantidad = progress.cantidadPrimos,
                        progreso = progress.porcentaje
                    )
                }

                val tiempo = System.currentTimeMillis() - inicio

                _primos.value = PrimosUi.Listo(
                    cantidad = ultimoProgreso.cantidadPrimos,
                    milisegundos = tiempo
                )

                // Guardar resultado en historial DataStore
                val nuevoItem = HistorialItem(
                    id = UUID.randomUUID().toString(),
                    tipo = TipoOperacion.PRIMOS,
                    titulo = "Cálculo de primos (N = $hasta)",
                    detalle = "${ultimoProgreso.cantidadPrimos} primos encontrados",
                    milisegundos = tiempo,
                    fechaHora = obtenerFechaHoraActual()
                )
                preferencesRepo.agregarHistorial(nuevoItem)

                emitirEventoUi("Cálculo de primos completado")

            } catch (e: CancellationException) {
                _primos.value = PrimosUi.Cancelado(
                    procesados = ultimoProgreso.procesados,
                    limite = ultimoProgreso.limite,
                    cantidad = ultimoProgreso.cantidadPrimos,
                    progreso = ultimoProgreso.porcentaje
                )
                emitirEventoUi("Cálculo de primos cancelado")
                throw e
            }
        }
    }

    fun cancelarPrimos() {
        primosJob?.cancel()
    }

    // Dos cargas: pasos 6 y 7 con Reintentos y Timeout
    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()

    private val _simularError = MutableStateFlow(false)
    val simularError: StateFlow<Boolean> = _simularError.asStateFlow()

    private var cargaJob: Job? = null

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
        if (_carga.value is CargaUi.Cargando) return

        cargaJob = viewModelScope.launch {
            val maxIntentos = 3
            val inicioTotal = System.currentTimeMillis()
            var ultimoError: Exception? = null

            for (intento in 1..maxIntentos) {
                _carga.value = CargaUi.Cargando(
                    intentoActual = intento,
                    maxIntentos = maxIntentos,
                    mensajeEspera = if (intento > 1) "Ejecutando intento $intento de $maxIntentos..." else null
                )

                try {
                    val (perfil, pedidos) = withTimeout(7000) {
                        if (paralela) {
                            coroutineScope {
                                val perfilAsync = async { repo.cargarPerfil() }
                                val pedidosAsync = async { repo.cargarPedidos(_simularError.value) }
                                Pair(perfilAsync.await(), pedidosAsync.await())
                            }
                        } else {
                            val perfil = repo.cargarPerfil()
                            val pedidos = repo.cargarPedidos(_simularError.value)
                            Pair(perfil, pedidos)
                        }
                    }

                    val tiempoTotal = System.currentTimeMillis() - inicioTotal
                    _carga.value = CargaUi.Exito(
                        modo = if (paralela) "A la vez" else "Una por una",
                        perfil = perfil,
                        pedidos = pedidos.size,
                        milisegundos = tiempoTotal
                    )

                    // Guardar resultado y tiempos en DataStore
                    val tipoOp = if (paralela) TipoOperacion.CARGA_PARALELA else TipoOperacion.CARGA_SECUENCIAL
                    val tituloOp = if (paralela) "Carga paralela (A la vez)" else "Carga secuencial (Una por una)"
                    val nuevoItem = HistorialItem(
                        id = UUID.randomUUID().toString(),
                        tipo = tipoOp,
                        titulo = tituloOp,
                        detalle = "Perfil ($perfil) + ${pedidos.size} pedidos",
                        milisegundos = tiempoTotal,
                        fechaHora = obtenerFechaHoraActual()
                    )
                    preferencesRepo.agregarHistorial(nuevoItem)
                    preferencesRepo.guardarTiempoCarga(paralela, tiempoTotal)

                    emitirEventoUi("Carga completada en $tiempoTotal ms")
                    return@launch

                } catch (e: TimeoutCancellationException) {
                    ultimoError = Exception("Tiempo de espera agotado (> 7s)")
                } catch (e: CancellationException) {
                    _carga.value = CargaUi.Inicial
                    throw e
                } catch (e: Exception) {
                    ultimoError = e
                }

                // Exponential Backoff: si falló y no es el último intento
                if (intento < maxIntentos) {
                    val esperaSegundos = if (intento == 1) 1 else 2
                    _carga.value = CargaUi.Cargando(
                        intentoActual = intento + 1,
                        maxIntentos = maxIntentos,
                        mensajeEspera = "Fallo en intento $intento. Esperando ${esperaSegundos}s para reintentar..."
                    )
                    delay(esperaSegundos * 1000L)
                }
            }

            val mensajeFinal = ultimoError?.message ?: "Error tras $maxIntentos intentos"
            _carga.value = CargaUi.Error("Error tras $maxIntentos intentos: $mensajeFinal")
            emitirEventoUi("No se pudo completar la carga")
        }
    }
}
