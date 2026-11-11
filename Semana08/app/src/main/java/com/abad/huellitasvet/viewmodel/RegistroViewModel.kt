package com.abad.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val repository: VetRepository
) : ViewModel() {

    private val _nombre = MutableStateFlow("")
    val nombre = _nombre.asStateFlow()

    private val _correo = MutableStateFlow("")
    val correo = _correo.asStateFlow()

    private val _telefono = MutableStateFlow("")
    val telefono = _telefono.asStateFlow()

    private val _distrito = MutableStateFlow("Miraflores")
    val distrito = _distrito.asStateFlow()

    private val _distritoPersonalizado = MutableStateFlow("")
    val distritoPersonalizado = _distritoPersonalizado.asStateFlow()

    private val _clave = MutableStateFlow("")
    val clave = _clave.asStateFlow()

    private val _confirmarClave = MutableStateFlow("")
    val confirmarClave = _confirmarClave.asStateFlow()

    private val _rol = MutableStateFlow("CLIENTE")
    val rol = _rol.asStateFlow()

    private val _errores = MutableStateFlow<Map<String, String>>(emptyMap())
    val errores = _errores.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando = _cargando.asStateFlow()

    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso = _registroExitoso.asStateFlow()

    private val _mensajeExito = MutableStateFlow<String?>(null)
    val mensajeExito = _mensajeExito.asStateFlow()

    fun onNombreChange(v: String) { _nombre.value = v }
    fun onCorreoChange(v: String) { _correo.value = v }
    fun onTelefonoChange(v: String) { _telefono.value = v }
    fun onDistritoChange(v: String) { _distrito.value = v }
    fun onDistritoPersonalizadoChange(v: String) { _distritoPersonalizado.value = v }
    fun onClaveChange(v: String) { _clave.value = v }
    fun onConfirmarClaveChange(v: String) { _confirmarClave.value = v }
    fun onRolChange(v: String) {
        if (v == "CLIENTE" || v == "VET") {
            _rol.value = v
        }
    }

    fun registrar() {
        val errs = mutableMapOf<String, String>()

        val nombreTrim = _nombre.value.trim()
        if (nombreTrim.length < 3) {
            errs["nombre"] = "El nombre debe tener al menos 3 caracteres"
        }

        val correoTrim = _correo.value.trim()
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        if (correoTrim.isBlank() || !emailRegex.matches(correoTrim)) {
            errs["correo"] = "Ingresa un correo electrónico válido"
        }

        val telefonoTrim = _telefono.value.trim()
        val phoneRegex = Regex("^9\\d{8}$")
        if (!phoneRegex.matches(telefonoTrim)) {
            errs["telefono"] = "El teléfono debe tener 9 dígitos y empezar con 9"
        }

        val distritoFinal = if (_distrito.value == "Otros") {
            _distritoPersonalizado.value.trim()
        } else {
            _distrito.value
        }
        if (distritoFinal.isBlank()) {
            errs["distrito"] = "Selecciona o ingresa un distrito"
        }

        val password = _clave.value
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        if (password.length < 8 || !hasLetter || !hasDigit) {
            errs["clave"] = "Mínimo 8 caracteres, al menos una letra y un número"
        }

        if (password != _confirmarClave.value) {
            errs["confirmarClave"] = "Las contraseñas no coinciden"
        }

        if (_rol.value !in listOf("CLIENTE", "VET")) {
            errs["rol"] = "Selecciona un rol válido"
        }

        _errores.value = errs
        if (errs.isNotEmpty()) return

        viewModelScope.launch {
            _cargando.value = true
            _mensajeExito.value = null
            try {
                val existente = repository.obtenerUsuarioPorCorreo(correoTrim)
                if (existente != null) {
                    _errores.value = mapOf("correo" to "Este correo ya está registrado")
                    return@launch
                }

                repository.registrarUsuario(
                    nombre = nombreTrim,
                    correo = correoTrim,
                    clave = password,
                    rol = _rol.value,
                    telefono = telefonoTrim,
                    distrito = distritoFinal
                )

                _mensajeExito.value = "¡Registro exitoso! Ya puedes iniciar sesión."
                _registroExitoso.value = true
            } catch (e: Exception) {
                _errores.value = mapOf("correo" to (e.message ?: "Error al registrar usuario"))
            } finally {
                _cargando.value = false
            }
        }
    }
}
