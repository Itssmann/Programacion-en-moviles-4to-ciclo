package com.abad.pokedex

import android.content.Context
import androidx.room.Room
import com.abad.pokedex.data.local.AppDatabase
import com.abad.pokedex.data.remote.RetrofitClient
import com.abad.pokedex.data.repository.PokemonRepository
import com.abad.pokedex.data.repository.PokemonRepositoryImpl

class AppContainer(context: Context) {

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "pokedex_abad.db"
        ).build()
    }

    val pokemonRepository: PokemonRepository by lazy {
        PokemonRepositoryImpl(
            api = RetrofitClient.api,
            dao = database.pokemonDao()
        )
    }
}

