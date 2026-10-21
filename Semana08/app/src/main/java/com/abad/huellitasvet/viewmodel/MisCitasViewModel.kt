package com.abad.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.huellitasvet.data.local.entity.Cita
import com.abad.huellitasvet.data.local.relation.CitaConDetalle
import com.abad.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

class MisCitasViewModel(
    private val repository: VetRepository,
    duenoId: Int
) : ViewModel() {

    private val citas = repository.observarCitasDueno(duenoId)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _ahora = MutableStateFlow(LocalDateTime.now())

    private fun fechaHora(cita: Cita): LocalDateTime? =
        runCatching {
            LocalDateTime.parse("${cita.fecha}T${cita.hora}")
        }.getOrNull()

    private fun esProxima(cita: Cita): Boolean {
        val inicio = fechaHora(cita) ?: return false
        return cita.estado in listOf("PENDIENTE", "CONFIRMADA") &&
                inicio.isAfter(_ahora.value)
    }

    val proximas: StateFlow<List<CitaConDetalle>> =
        combine(citas, _ahora) { lista, _ ->
            lista.filter { esProxima(it.cita) }
                .sortedWith(compareBy({ it.cita.fecha }, { it.cita.hora }))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val historial: StateFlow<List<CitaConDetalle>> =
        combine(citas, _ahora) { lista, _ ->
            lista.filterNot { esProxima(it.cita) }
                .sortedWith(
                    compareByDescending<CitaConDetalle> { it.cita.fecha }
                        .thenByDescending { it.cita.hora }
                )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()

    fun actualizarHora() {
        _ahora.value = LocalDateTime.now()
    }

    fun puedeCancelar(cita: Cita): Boolean {
        if (cita.estado !in listOf("PENDIENTE", "CONFIRMADA")) {
            return false
        }

        val inicio = fechaHora(cita) ?: return false
        return Duration.between(LocalDateTime.now(), inicio).toMinutes() > 120
    }

    fun cancelar(cita: Cita) {
        viewModelScope.launch {
            actualizarHora()

            if (!puedeCancelar(cita)) {
                _mensaje.value = "Ya no puedes cancelar esta cita"
                return@launch
            }

            try {
                // Releer el estado actual antes de cambiarlo.
                val actual = repository.observarCita(cita.id).first()?.cita
                if (actual == null || !puedeCancelar(actual)) {
                    _mensaje.value = "Ya no puedes cancelar esta cita"
                    return@launch
                }

                repository.cambiarEstadoCita(cita.id, "CANCELADA")
                _mensaje.value = "Cita cancelada correctamente"
            } catch (e: Exception) {
                _mensaje.value = e.message ?: "No se pudo cancelar"
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
