package com.abad.pokedex

import android.content.Context
import androidx.room.Room
import com.abad.pokedex.data.auth.SesionActual
import com.abad.pokedex.data.local.AppDatabase
import com.abad.pokedex.data.local.MIGRATION_1_2
import com.abad.pokedex.data.local.MIGRATION_2_3
import com.abad.pokedex.data.remote.RetrofitClient
import com.abad.pokedex.data.repository.PokemonRepository
import com.abad.pokedex.data.repository.PokemonRepositoryImpl

class AppContainer(context: Context) {

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "pokedex_abad.db"
        )
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3
            )
            .build()
    }

    val sesionActual: SesionActual by lazy {
        SesionActual.firebase()
    }

    val pokemonRepository: PokemonRepository by lazy {
        PokemonRepositoryImpl(
            api = RetrofitClient.api,
            dao = database.pokemonDao(),
            sesionActual = sesionActual
        )
    }
}
