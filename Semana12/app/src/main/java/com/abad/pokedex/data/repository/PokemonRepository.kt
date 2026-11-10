
package com.abad.pokedex.data.repository

import com.abad.pokedex.domain.model.Pokemon
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {

    val pokemones: Flow<List<Pokemon>>

    val favoritos: Flow<List<Pokemon>>

    suspend fun refrescar(tipo: String): Result<Int>

    suspend fun obtenerDetalle(
        nombre: String
    ): Result<Pokemon>

    suspend fun alternarFavorito(nombre: String)

    suspend fun cantidadGuardada(): Int

    suspend fun cantidadFavoritos(): Int

    suspend fun limpiarCache()
}
