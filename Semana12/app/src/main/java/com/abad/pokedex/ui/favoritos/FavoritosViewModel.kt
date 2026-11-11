package com.abad.pokedex.ui.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.pokedex.data.repository.PokemonRepository
import com.abad.pokedex.domain.model.Pokemon
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritosViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    val favoritos = repository.favoritos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList<Pokemon>()
    )

    fun alternarFavorito(nombre: String) {
        viewModelScope.launch {
            repository.alternarFavorito(nombre)
        }
    }
}
