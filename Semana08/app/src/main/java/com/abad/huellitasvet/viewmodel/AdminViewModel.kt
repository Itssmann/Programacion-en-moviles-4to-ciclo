package com.abad.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.huellitasvet.data.local.entity.Servicio
import com.abad.huellitasvet.data.local.entity.Usuario
import com.abad.huellitasvet.data.local.relation.CitaConDetalle
import com.abad.huellitasvet.data.local.relation.MascotaConDueno
import com.abad.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: VetRepository
) : ViewModel() {

    val usuarios: StateFlow<List<Usuario>> =
        repository.observarUsuarios()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mascotas: StateFlow<List<MascotaConDueno>> =
        repository.observarTodasMascotas()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val citas: StateFlow<List<CitaConDetalle>> =
        repository.observarTodasCitas()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val servicios: StateFlow<List<Servicio>> =
        repository.observarServicios()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _busquedaUsuario = MutableStateFlow("")
    val busquedaUsuario = _busquedaUsuario.asStateFlow()

    private val _filtroRol = MutableStateFlow("TODOS")
    val filtroRol = _filtroRol.asStateFlow()

    private val _filtroEstadoCita = MutableStateFlow("TODOS")
    val filtroEstadoCita = _filtroEstadoCita.asStateFlow()

    val usuariosFiltrados: StateFlow<List<Usuario>> =
        combine(usuarios, _busquedaUsuario, _filtroRol) { lista, query, rol ->
            lista.filter { u ->
                val coincideQuery = u.nombre.contains(query, ignoreCase = true) ||
                        u.correo.contains(query, ignoreCase = true)
                val coincideRol = rol == "TODOS" || u.rol == rol
                coincideQuery && coincideRol
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val citasFiltradas: StateFlow<List<CitaConDetalle>> =
        combine(citas, _filtroEstadoCita) { lista, estado ->
            lista.filter { c ->
                estado == "TODOS" || c.cita.estado == estado
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun buscarUsuario(query: String) {
        _busquedaUsuario.value = query
    }

    fun filtrarRol(rol: String) {
        _filtroRol.value = rol
    }

    fun filtrarEstadoCita(estado: String) {
        _filtroEstadoCita.value = estado
    }

    fun guardarServicio(servicio: Servicio) {
        viewModelScope.launch {
            repository.guardarServicio(servicio)
        }
    }

    fun eliminarServicio(servicio: Servicio) {
        viewModelScope.launch {
            repository.eliminarServicio(servicio)
        }
    }
}
