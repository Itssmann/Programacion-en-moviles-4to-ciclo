package com.abad.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.huellitasvet.data.local.entity.Notificacion
import com.abad.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotificacionesViewModel(
    private val repository: VetRepository,
    private val usuarioId: Int
) : ViewModel() {

    val notificaciones: StateFlow<List<Notificacion>> =
        repository.observarNotificaciones(usuarioId)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    val noLeidasCount: StateFlow<Int> =
        repository.contarNotificacionesNoLeidas(usuarioId)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0
            )

    fun marcarComoLeida(id: Int) {
        viewModelScope.launch {
            repository.marcarNotificacionLeida(id)
        }
    }

    fun marcarTodasComoLeidas() {
        viewModelScope.launch {
            repository.marcarTodasNotificacionesLeidas(usuarioId)
        }
    }

    fun eliminar(id: Int) {
        viewModelScope.launch {
            repository.eliminarNotificacion(id)
        }
    }
}
