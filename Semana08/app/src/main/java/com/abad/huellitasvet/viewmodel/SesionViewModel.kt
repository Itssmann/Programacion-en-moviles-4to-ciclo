package com.abad.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.huellitasvet.data.local.entity.Usuario
import com.abad.huellitasvet.data.prefs.PreferenciasStore
import com.abad.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class SesionViewModel(
    private val repository: VetRepository,
    private val preferencias: PreferenciasStore
) : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    val modoOscuro = preferencias.modoOscuro.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    val perfilActivo = preferencias.perfilActivo.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ""
    )

    val usuarioId = preferencias.usuarioId.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    fun login(
        correo: String,
        clave: String,
        rolSeleccionado: String
    ) {
        if (correo.isBlank() || clave.isBlank()) {
            _error.value = "Completa el correo y la contraseña"
            return
        }

        if (rolSeleccionado != "CLIENTE" && rolSeleccionado != "ADMIN") {
            _error.value = "Selecciona un perfil válido"
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                val usuario = repository.login(
                    correo.trim(),
                    clave
                )

                when {
                    usuario == null -> {
                        _error.value = "Correo o contraseña incorrectos"
                    }

                    usuario.rol != rolSeleccionado -> {
                        _error.value =
                            "Esta cuenta no corresponde al perfil seleccionado"
                    }

                    else -> {
                        preferencias.guardarSesion(usuario.rol, usuario.id)
                        _usuarioActual.value = usuario
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al iniciar sesión"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            preferencias.cerrarSesion()
            _usuarioActual.value = null
            _error.value = null
        }
    }

    fun cambiarModoOscuro(activado: Boolean) {
        viewModelScope.launch {
            preferencias.guardarModoOscuro(activado)
        }
    }
}

