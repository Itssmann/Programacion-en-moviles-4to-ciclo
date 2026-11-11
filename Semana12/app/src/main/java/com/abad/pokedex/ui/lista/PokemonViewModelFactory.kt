package com.abad.pokedex.ui.lista

import androidx.lifecycle.ViewModel
import com.abad.pokedex.notification.Notificador
import androidx.lifecycle.ViewModelProvider
import com.abad.pokedex.data.repository.PokemonRepository

class PokemonViewModelFactory(
    private val repository: PokemonRepository,
    private val notificador: Notificador
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        require(
            modelClass.isAssignableFrom(PokemonViewModel::class.java)
        )

        return PokemonViewModel(repository, notificador) as T
    }
}
