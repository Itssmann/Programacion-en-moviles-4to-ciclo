package com.abad.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.huellitasvet.data.local.relation.CitaConDetalle
import com.abad.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AtenderCitaViewModel(
    private val repository: VetRepository,
    private val citaId: Int
) : ViewModel() {

    val cita: StateFlow<CitaConDetalle?> =
        repository.observarCita(citaId)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )

    private val _errores = MutableStateFlow<Map<String, String>>(emptyMap())
    val errores = _errores.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()

    private val _registrada = MutableStateFlow(false)
    val registrada = _registrada.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando = _guardando.asStateFlow()

    fun registrar(
        diagnostico: String,
        tratamiento: String,
        peso: String
    ) {
        if (_guardando.value) return

        val erroresNuevos = mutableMapOf<String, String>()
        val pesoNumero = peso.toDoubleOrNull()

        if (diagnostico.trim().length < 5) {
            erroresNuevos["diagnostico"] = "Mínimo 5 caracteres"
        }
        if (pesoNumero == null || !pesoNumero.isFinite() || pesoNumero <= 0) {
            erroresNuevos["peso"] = "Ingresa un peso válido mayor a cero"
        }

        _errores.value = erroresNuevos
        if (erroresNuevos.isNotEmpty()) return

        viewModelScope.launch {
            _guardando.value = true
            try {
                val actual = repository.observarCita(citaId).first()?.cita
                require(actual?.estado in listOf("PENDIENTE", "CONFIRMADA")) {
                    "Esta cita ya no puede atenderse"
                }

                repository.registrarAtencion(
                    id = citaId,
                    diagnostico = diagnostico.trim(),
                    tratamiento = tratamiento.trim(),
                    peso = pesoNumero!!
                )
                _registrada.value = true
            } catch (e: Exception) {
                _mensaje.value = e.message ?: "No se pudo registrar"
            } finally {
                _guardando.value = false
            }
        }
    }

    fun cancelar() {
        viewModelScope.launch {
            try {
                val actual = repository.observarCita(citaId).first()?.cita
                require(actual?.estado in listOf("PENDIENTE", "CONFIRMADA")) {
                    "Esta cita ya no se puede cancelar"
                }
                repository.cambiarEstadoCita(citaId, "CANCELADA")
                _registrada.value = true
            } catch (e: Exception) {
                _mensaje.value = e.message ?: "No se pudo cancelar"
            }
        }
    }
}

