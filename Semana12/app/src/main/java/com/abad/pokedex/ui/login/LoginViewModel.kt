package com.abad.pokedex.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abad.pokedex.data.auth.AuthRepository
import com.abad.pokedex.domain.model.Sesion
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val sesion: Sesion? = null,
    val cargando: Boolean = false,
    val error: String? = null
)

class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(
        LoginUiState(
            sesion = repository.usuarioActual
        )
    )

    val estado = _estado.asStateFlow()

    private var trabajoAutenticacion: Job? = null

    init {
        if (repository.usuarioActual != null) {
            trabajoAutenticacion = viewModelScope.launch {
                _estado.value = _estado.value.copy(
                    cargando = true,
                    error = null
                )

                try {
                    val sesion = repository.obtenerSesionActualizada()

                    _estado.value = LoginUiState(
                        sesion = sesion
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _estado.value = LoginUiState(
                        sesion = repository.usuarioActual,
                        error = "No se pudo actualizar el rol. Comprueba tu conexión."
                    )
                }
            }
        }
    }

    fun iniciarSesion(
        correo: String,
        contrasena: String
    ) {
        if (_estado.value.cargando) return

        if (correo.isBlank() || contrasena.isBlank()) {
            mostrarError("Completa todos los campos.")
            return
        }

        ejecutarAutenticacion {
            repository.iniciarSesion(correo.trim(), contrasena)
        }
    }

    fun registrar(
        nombre: String,
        correo: String,
        contrasena: String,
        confirmar: String
    ) {
        if (_estado.value.cargando) return

        if (
            nombre.isBlank() ||
            correo.isBlank() ||
            contrasena.isBlank() ||
            confirmar.isBlank()
        ) {
            mostrarError("Completa todos los campos.")
            return
        }

        if (nombre.trim().length < 2) {
            mostrarError("Ingresa un nombre válido.")
            return
        }

        if (contrasena.length < 6) {
            mostrarError(
                "La contraseña debe tener al menos 6 caracteres."
            )
            return
        }

        if (contrasena != confirmar) {
            mostrarError("Las contraseñas no coinciden.")
            return
        }

        ejecutarAutenticacion {
            repository.registrar(
                nombre = nombre.trim(),
                correo = correo.trim(),
                contrasena = contrasena
            )
        }
    }

    private fun ejecutarAutenticacion(
        operacion: suspend () -> Result<Sesion>
    ) {
        trabajoAutenticacion = viewModelScope.launch {
            _estado.value = _estado.value.copy(
                cargando = true,
                error = null
            )

            val resultado = try {
                operacion()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }

            _estado.value = resultado.fold(
                onSuccess = { sesion ->
                    LoginUiState(sesion = sesion)
                },
                onFailure = { error ->
                    LoginUiState(
                        sesion = repository.usuarioActual,
                        error = mensajeError(error)
                    )
                }
            )
        }
    }

    fun cerrarSesion() {
        trabajoAutenticacion?.cancel()
        repository.cerrarSesion()
        _estado.value = LoginUiState()
    }

    fun limpiarError() {
        _estado.value = _estado.value.copy(error = null)
    }

    private fun mostrarError(mensaje: String) {
        _estado.value = _estado.value.copy(error = mensaje)
    }

    private fun mensajeError(error: Throwable): String {
        val codigo = (error as? FirebaseAuthException)?.errorCode

        return when (codigo) {
            "ERROR_EMAIL_ALREADY_IN_USE" ->
                "Este correo ya está registrado."

            "ERROR_INVALID_EMAIL" ->
                "Ingresa un correo válido."

            "ERROR_WRONG_PASSWORD",
            "ERROR_INVALID_CREDENTIAL",
            "ERROR_USER_NOT_FOUND" ->
                "Correo o contraseña incorrectos."

            "ERROR_WEAK_PASSWORD" ->
                "La contraseña es demasiado débil."

            "ERROR_NETWORK_REQUEST_FAILED" ->
                "Comprueba tu conexión a internet."

            else ->
                error.localizedMessage
                    ?: "No se pudo completar la autenticación."
        }
    }
}

class LoginViewModelFactory(
    private val repository: AuthRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
