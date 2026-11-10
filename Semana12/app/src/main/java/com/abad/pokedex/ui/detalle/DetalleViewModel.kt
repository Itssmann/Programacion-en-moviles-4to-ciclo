package com.abad.pokedex.ui.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.pokedex.data.repository.PokemonRepository
import com.abad.pokedex.domain.model.Pokemon
import com.abad.pokedex.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetalleViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _estado =
        MutableStateFlow<UiState<Pokemon>>(
            UiState.Cargando
        )

    val estado: StateFlow<UiState<Pokemon>> = _estado

    fun cargarDetalle(nombre: String) {
        viewModelScope.launch {
            _estado.value = UiState.Cargando

            repository.obtenerDetalle(nombre)
                .onSuccess { pokemon ->
                    _estado.value = UiState.Exito(pokemon)
                }
                .onFailure { error ->
                    _estado.value = UiState.Error(
                        error.message ?: "Error al obtener detalle"
                    )
                }
        }
    }
}
