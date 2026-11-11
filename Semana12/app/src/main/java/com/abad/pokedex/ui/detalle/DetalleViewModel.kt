package com.abad.pokedex.ui.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.pokedex.data.repository.PokemonRepository
import com.abad.pokedex.domain.model.Pokemon
import com.abad.pokedex.ui.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DetalleViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _estado =
        MutableStateFlow<UiState<Pokemon>>(UiState.Cargando)
    val estado: StateFlow<UiState<Pokemon>> = _estado.asStateFlow()
    private var detalleJob: Job? = null

    fun cargarDetalle(nombre: String) {
        detalleJob?.cancel()
        _estado.value = UiState.Cargando
        detalleJob = viewModelScope.launch {
            launch {
                repository.observarDetalle(nombre)
                    .catch { error ->
                        _estado.value = UiState.Error(
                            error.message ?: "Error al leer el detalle guardado"
                        )
                    }
                    .collect { pokemon ->
                        _estado.value = if (pokemon != null) {
                            UiState.Exito(pokemon)
                        } else if (_estado.value is UiState.Error) {
                            _estado.value
                        } else {
                            UiState.Cargando
                        }
                    }
            }
            // La red actualiza Room; solo su flujo publica datos en la pantalla.
            repository.obtenerDetalle(nombre).onFailure { error ->
                if (_estado.value !is UiState.Exito) {
                    _estado.value = UiState.Error(
                        error.message ?: "Error al obtener detalle"
                    )
                }
            }
        }
    }

    fun alternarFavorito() {
        val actual = _estado.value as? UiState.Exito ?: return
        viewModelScope.launch {
            repository.alternarFavorito(actual.datos.nombre)
        }
    }
}
