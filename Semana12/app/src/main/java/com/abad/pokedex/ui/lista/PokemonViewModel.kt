package com.abad.pokedex.ui.lista

import androidx.lifecycle.ViewModel
import com.abad.pokedex.notification.Notificador
import androidx.lifecycle.viewModelScope
import com.abad.pokedex.data.repository.PokemonRepository
import com.abad.pokedex.domain.model.Pokemon
import com.abad.pokedex.ui.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class PokemonViewModel(
    private val repository: PokemonRepository,
    private val notificador: Notificador
) : ViewModel() {
    private val _estado = MutableStateFlow<UiState<List<Pokemon>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Pokemon>>> = _estado.asStateFlow()

    private val _tipoActual = MutableStateFlow("fire")
    val tipoActual: StateFlow<String> = _tipoActual.asStateFlow()

    private val _tipoSeleccionado = MutableStateFlow("all")
    val tipoSeleccionado: StateFlow<String> = _tipoSeleccionado.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _ordenSeleccionado = MutableStateFlow("Numero")
    val ordenSeleccionado: StateFlow<String> = _ordenSeleccionado.asStateFlow()

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()
    private val _refrescando = MutableStateFlow(false)
    val refrescando: StateFlow<Boolean> = _refrescando.asStateFlow()

    private data class Sincronizacion(val enCurso: Boolean, val error: Throwable? = null)
    private val sincronizaciones = mutableMapOf<String, Sincronizacion>()
    private var locales: List<Pokemon>? = null
    private var errorLocal: Throwable? = null

    init {
        viewModelScope.launch {
            try {
                repository.pokemones.collect {
                    locales = it
                    errorLocal = null
                    publicarEstado()
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                errorLocal = error
                publicarEstado()
            }
        }
        cargar("fire")
    }

    fun cargar(tipo: String) {
        if (tipo != "all") {
            require(tipo in setOf("fire", "water", "grass"))
            _tipoActual.value = tipo
        }
        _tipoSeleccionado.value = tipo
        if (tipo != "all" && tipo !in sincronizaciones) {
            sincronizar(tipo)
        } else {
            publicarEstado()
        }
    }

    fun setTipo(tipo: String) {
        cargar(tipo)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        publicarEstado()
    }

    fun setOrden(orden: String) {
        _ordenSeleccionado.value = orden
        publicarEstado()
    }

    fun refrescar() {
        val tipo = _tipoActual.value
        sincronizar(tipo)
    }

    private fun sincronizar(tipo: String) {
        if (sincronizaciones[tipo]?.enCurso == true) return
        val errorAnterior = sincronizaciones[tipo]?.error
        sincronizaciones[tipo] = Sincronizacion(enCurso = true, error = errorAnterior)
        publicarEstado()
        viewModelScope.launch {
            val resultado = try {
                repository.refrescar(tipo)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Result.failure(error)
            }
            try {
                locales = repository.pokemones.first()
                errorLocal = null
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                errorLocal = error
            }
            sincronizaciones[tipo] = Sincronizacion(false, resultado.exceptionOrNull())
            publicarEstado()
            resultado.onSuccess { n ->
                notificador.mostrar("Datos actualizados", "Se guardaron $n elementos")
            }
        }
    }

    private fun publicarEstado() {
        val tipo = _tipoSeleccionado.value
        val query = _searchQuery.value.trim().lowercase()
        val orden = _ordenSeleccionado.value

        val listaBase = locales.orEmpty()

        val filtradosPorTipo = if (tipo == "all") {
            listaBase
        } else {
            listaBase.filter { tipo in it.tipos }
        }

        val filtradosPorBusqueda = if (query.isEmpty()) {
            filtradosPorTipo
        } else {
            filtradosPorTipo.filter { it.nombre.lowercase().contains(query) }
        }

        val datos = when (orden) {
            "Nombre" -> filtradosPorBusqueda.sortedBy { it.nombre }
            else -> filtradosPorBusqueda.sortedBy { pokemon ->
                val regex = "/pokemon/(\\d+)\\.png".toRegex()
                regex.find(pokemon.imagenUrl.orEmpty())?.groupValues?.get(1)?.toIntOrNull() ?: 0
            }
        }

        val sincronizacion = sincronizaciones[_tipoActual.value]
        val error = sincronizacion?.error
        _aviso.value = if (datos.isNotEmpty() && error != null) {
            "Sin conexión. Mostrando datos guardados"
        } else null

        _estado.value = when {
            datos.isNotEmpty() -> UiState.Exito(datos)
            errorLocal != null -> UiState.Error("No se pudieron leer los datos guardados. Reintenta.")
            locales == null || (tipo != "all" && tipo !in sincronizaciones) || (sincronizacion?.enCurso == true && listaBase.isEmpty()) -> UiState.Cargando
            error != null -> UiState.Error(mensajeDe(error))
            else -> UiState.Exito(emptyList())
        }
        _refrescando.value = sincronizacion?.enCurso == true
    }

    private fun mensajeDe(error: Throwable): String = when (error) {
        is IOException -> "No hay conexión a internet. Reintenta cuando tengas conexión."
        is HttpException -> "El servidor respondió con error ${error.code()}. Intenta nuevamente."
        else -> "No se pudieron actualizar los Pokémon. Intenta nuevamente."
    }

    fun alternarFavorito(nombre: String) {
        viewModelScope.launch { repository.alternarFavorito(nombre) }
    }
}
