package com.abad.pokedex.ui.lista

import androidx.lifecycle.ViewModel
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
    private val repository: PokemonRepository
) : ViewModel() {
    private val _estado = MutableStateFlow<UiState<List<Pokemon>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Pokemon>>> = _estado.asStateFlow()
    private val _tipoActual = MutableStateFlow("fire")
    val tipoActual: StateFlow<String> = _tipoActual.asStateFlow()
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

    // Seleccionar un tipo ya consultado no repite la descarga (incluidos errores y vacíos).
    fun cargar(tipo: String) {
        require(tipo in setOf("fire", "water", "grass"))
        _tipoActual.value = tipo
        if (tipo !in sincronizaciones) sincronizar(tipo) else publicarEstado()
    }

    fun refrescar() = sincronizar(_tipoActual.value)

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
            // Leer Room después del refresco evita declarar vacío antes de recibir su emisión.
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
        }
    }

    private fun publicarEstado() {
        val sincronizacion = sincronizaciones[_tipoActual.value]
        val datos = locales.orEmpty().filter { _tipoActual.value in it.tipos }
        val error = sincronizacion?.error
        _aviso.value = if (datos.isNotEmpty() && error != null) {
            "Sin conexión. Mostrando datos guardados"
        } else null
        _estado.value = when {
            datos.isNotEmpty() -> UiState.Exito(datos)
            errorLocal != null -> UiState.Error("No se pudieron leer los datos guardados. Reintenta.")
            locales == null || sincronizacion == null || sincronizacion.enCurso -> UiState.Cargando
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
