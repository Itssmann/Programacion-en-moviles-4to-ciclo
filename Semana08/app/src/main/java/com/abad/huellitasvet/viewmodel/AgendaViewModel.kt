package com.abad.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.huellitasvet.data.local.entity.Cita
import com.abad.huellitasvet.data.local.relation.CitaConDetalle
import com.abad.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class AgendaViewModel(
    private val repository: VetRepository
) : ViewModel() {

    private val _fechaSeleccionada = MutableStateFlow(LocalDate.now())
    val fechaSeleccionada = _fechaSeleccionada.asStateFlow()

    val citas: StateFlow<List<CitaConDetalle>> =
        _fechaSeleccionada.flatMapLatest { fecha ->
            repository.observarCitasFecha(fecha.toString())
        }.map { lista ->
            lista.sortedBy { it.cita.hora }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()

    fun seleccionarFecha(fecha: LocalDate) {
        _fechaSeleccionada.value = fecha
    }

    fun confirmar(cita: Cita) {
        viewModelScope.launch {
            try {
                val actual = repository.observarCita(cita.id).first()?.cita
                if (actual?.estado != "PENDIENTE") {
                    _mensaje.value = "Solo se confirman citas pendientes"
                    return@launch
                }
                repository.cambiarEstadoCita(cita.id, "CONFIRMADA")
                _mensaje.value = "Cita confirmada"
            } catch (e: Exception) {
                _mensaje.value = e.message ?: "No se pudo confirmar"
            }
        }
    }

    fun cancelar(cita: Cita) {
        viewModelScope.launch {
            try {
                val actual = repository.observarCita(cita.id).first()?.cita
                if (actual?.estado !in listOf("PENDIENTE", "CONFIRMADA")) {
                    _mensaje.value = "Esta cita no se puede cancelar"
                    return@launch
                }
                repository.cambiarEstadoCita(cita.id, "CANCELADA")
                _mensaje.value = "Cita cancelada"
            } catch (e: Exception) {
                _mensaje.value = e.message ?: "No se pudo cancelar"
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}

