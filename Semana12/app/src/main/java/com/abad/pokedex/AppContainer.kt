package com.abad.pokedex

import com.abad.pokedex.data.remote.RetrofitClient
import com.abad.pokedex.data.repository.PokemonRepository
import com.abad.pokedex.data.repository.PokemonRepositoryImpl

class AppContainer {

    val pokemonRepository: PokemonRepository by lazy {
        PokemonRepositoryImpl(RetrofitClient.api)
    }
}
