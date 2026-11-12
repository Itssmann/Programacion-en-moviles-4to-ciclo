package com.abad.pokedex.ui.admin

import androidx.lifecycle.ViewModel
import com.abad.pokedex.notification.Notificador
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abad.pokedex.data.auth.AuthRepository
import com.abad.pokedex.data.repository.PokemonRepository
import com.abad.pokedex.domain.model.Rol
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class OperacionAdmin { SINCRONIZAR, LIMPIAR }

data class AdminUiState(
    val totalPokemones: Int = 0,
    val totalFavoritos: Int = 0,
    val cargando: Boolean = false,
    val mensaje: String? = null,
    val esError: Boolean = false,
    val datosCargados: Boolean = false,
    val distribucionTipos: Map<String, Int> = emptyMap(),
    val operacion: OperacionAdmin? = null
)

class AdminViewModel(
    private val repository: PokemonRepository,
    private val authRepository: AuthRepository,
    private val notificador: Notificador
) : ViewModel() {

    private val _estado = MutableStateFlow(AdminUiState())
    val estado = _estado.asStateFlow()

    init {
        actualizarTotales()
        viewModelScope.launch {
            try {
                combine(repository.pokemones, repository.favoritos) { catalogo, _ ->
                    Triple(catalogo.size, repository.cantidadFavoritos(),
                        catalogo.flatMap { it.tipos.distinct() }.groupingBy { it }.eachCount()
                            .toList().sortedByDescending { it.second }.toMap())
                }.collect { (total, favoritos, tipos) ->
                    _estado.update { it.copy(totalPokemones = total, totalFavoritos = favoritos,
                        distribucionTipos = tipos, datosCargados = true) }
                }
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) {
                _estado.update { it.copy(mensaje = "No se pudo actualizar el resumen del catálogo.", esError = true) }
            }
        }
    }

    fun actualizarTotales() {
        viewModelScope.launch {
            try {
                _estado.value = _estado.value.copy(
                    totalPokemones = repository.cantidadGuardada(),
                    totalFavoritos = repository.cantidadFavoritos(),
                    datosCargados = true
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    mensaje = "No se pudieron consultar los datos.", esError = true
                )
            }
        }
    }

    private suspend fun verificarAdministrador(): Boolean {
        val sesion = authRepository.obtenerSesionActualizada()
        return sesion?.rol == Rol.ADMIN
    }

    fun limpiarCache() {
        if (_estado.value.cargando) return

        viewModelScope.launch {
            _estado.value = _estado.value.copy(
                cargando = true,
                mensaje = null,
                esError = false,
                operacion = OperacionAdmin.LIMPIAR
            )

            try {
                if (!verificarAdministrador()) {
                    _estado.value = _estado.value.copy(
                        cargando = false, operacion = null, esError = true,
                        mensaje = "Acceso denegado: se requiere una cuenta administradora."
                    )
                    return@launch
                }

                repository.limpiarCache()

                _estado.value = _estado.value.copy(
                    cargando = false, operacion = null, esError = false, datosCargados = true,
                    totalPokemones = repository.cantidadGuardada(),
                    totalFavoritos = repository.cantidadFavoritos(),
                    mensaje = "Caché eliminada. Los favoritos se conservaron."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    cargando = false, operacion = null, esError = true,
                    mensaje = "No se pudo limpiar la caché."
                )
            }
        }
    }

    fun sincronizar() {
        if (_estado.value.cargando) return

        viewModelScope.launch {
            _estado.value = _estado.value.copy(
                cargando = true,
                mensaje = null,
                esError = false,
                operacion = OperacionAdmin.SINCRONIZAR
            )

            try {
                if (!verificarAdministrador()) {
                    _estado.value = _estado.value.copy(
                        cargando = false, operacion = null, esError = true,
                        mensaje = "Acceso denegado: se requiere una cuenta administradora."
                    )
                    return@launch
                }

                val resultado = repository.sincronizarCatalogo()
                resultado.onSuccess { n ->
                    notificador.mostrar("Datos actualizados", "Se guardaron $n elementos")
                }

                _estado.value = if (resultado.isSuccess) {
                    _estado.value.copy(
                        cargando = false, operacion = null, esError = false, datosCargados = true,
                        totalPokemones = repository.cantidadGuardada(),
                        totalFavoritos = repository.cantidadFavoritos(),
                        mensaje = "Sincronización completada: " +
                                "${resultado.getOrDefault(0)} Pokémon descargados."
                    )
                } else {
                    _estado.value.copy(
                        cargando = false, operacion = null, esError = true,
                        mensaje = "No se pudo sincronizar. Comprueba tu conexión."
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    cargando = false, operacion = null, esError = true,
                    mensaje = "Ocurrió un error durante la sincronización."
                )
            }
        }
    }
}

class AdminViewModelFactory(
    private val repository: PokemonRepository,
    private val authRepository: AuthRepository,
    private val notificador: Notificador
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            return AdminViewModel(
                repository = repository,
                authRepository = authRepository,
                notificador = notificador
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
