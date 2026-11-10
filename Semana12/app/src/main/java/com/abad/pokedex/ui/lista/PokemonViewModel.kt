package com.abad.pokedex.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.pokedex.data.repository.PokemonRepository
import com.abad.pokedex.domain.model.Pokemon
import com.abad.pokedex.ui.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PokemonViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _estado =
        MutableStateFlow<UiState<List<Pokemon>>>(
            UiState.Cargando
        )

    val estado: StateFlow<UiState<List<Pokemon>>> = _estado

    private var cargaJob: Job? = null

    fun cargar(tipo: String) {
        cargaJob?.cancel()

        cargaJob = viewModelScope.launch {
            _estado.value = UiState.Cargando

            try {
                val resultado = repository.refrescar(tipo)

                resultado
                    .onSuccess {
                        val lista = repository.pokemones.first()
                        _estado.value = UiState.Exito(lista)
                    }
                    .onFailure { error ->
                        _estado.value = UiState.Error(
                            error.message ?: "No se pudieron cargar los Pokémon"
                        )
                    }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _estado.value = UiState.Error(
                    error.message ?: "Error de conexión"
                )
            }
        }
    }
}
