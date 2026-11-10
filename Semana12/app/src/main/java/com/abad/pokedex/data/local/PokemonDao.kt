package com.abad.pokedex.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    @Query("SELECT * FROM pokemones ORDER BY nombre ASC")
    fun observarTodos(): Flow<List<PokemonEntity>>

    @Query(
        "SELECT * FROM pokemones WHERE esFavorito = 1 ORDER BY nombre ASC"
    )
    fun observarFavoritos(): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemones WHERE nombre = :nombre LIMIT 1")
    suspend fun obtenerPorNombre(
        nombre: String
    ): PokemonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarTodos(
        pokemones: List<PokemonEntity>
    )

    @Query(
        "UPDATE pokemones SET esFavorito = :favorito WHERE nombre = :nombre"
    )
    suspend fun actualizarFavorito(
        nombre: String,
        favorito: Boolean
    )

    @Query("SELECT COUNT(*) FROM pokemones")
    suspend fun contarTodos(): Int

    @Query("SELECT COUNT(*) FROM pokemones WHERE esFavorito = 1")
    suspend fun contarFavoritos(): Int

    @Query("DELETE FROM pokemones")
    suspend fun eliminarTodos()
}
