package com.abad.pokedex.ui.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abad.pokedex.data.repository.PokemonRepository

class DetalleViewModelFactory(
    private val repository: PokemonRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        require(
            modelClass.isAssignableFrom(DetalleViewModel::class.java)
        )
        return DetalleViewModel(repository) as T
    }
}
