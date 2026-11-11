package com.abad.pokedex.ui.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abad.pokedex.data.repository.PokemonRepository

class FavoritosViewModelFactory(
    private val repository: PokemonRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        require(
            modelClass.isAssignableFrom(FavoritosViewModel::class.java)
        )
        return FavoritosViewModel(repository) as T
    }
}
