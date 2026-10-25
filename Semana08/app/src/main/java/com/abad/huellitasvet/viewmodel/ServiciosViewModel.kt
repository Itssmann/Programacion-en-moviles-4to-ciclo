package com.abad.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.huellitasvet.data.local.entity.Servicio
import com.abad.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ServiciosViewModel(
    private val repository: VetRepository
) : ViewModel() {

    val servicios = repository.observarServicios()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()

    private val _guardado = MutableStateFlow(false)
    val guardado = _guardado.asStateFlow()

    fun limpiarEstado() {
        _mensaje.value = null
        _guardado.value = false
    }

    fun guardar(servicio: Servicio) {
        if (servicio.nombre.isBlank()) {
            _mensaje.value = "Ingresa el nombre del servicio"
            return
        }
        if (servicio.duracionMin !in listOf(15, 30, 45, 60)) {
            _mensaje.value = "Selecciona una duración válida"
            return
        }
        if (!servicio.precio.isFinite() || servicio.precio <= 0) {
            _mensaje.value = "El precio debe ser mayor a cero"
            return
        }

        viewModelScope.launch {
            try {
                repository.guardarServicio(servicio)
                _guardado.value = true
            } catch (e: Exception) {
                _mensaje.value = e.message ?: "No se pudo guardar"
            }
        }
    }

    fun eliminar(servicio: Servicio) {
        viewModelScope.launch {
            try {
                if (repository.servicioTieneCitas(servicio.id)) {
                    repository.guardarServicio(
                        servicio.copy(activo = false)
                    )
                    _mensaje.value =
                        "El servicio tiene citas: se desactivó en lugar de eliminarlo"
                } else {
                    repository.eliminarServicio(servicio)
                    _mensaje.value = "Servicio eliminado"
                }
            } catch (e: Exception) {
                _mensaje.value = e.message ?: "No se pudo eliminar"
            }
        }
    }
}
