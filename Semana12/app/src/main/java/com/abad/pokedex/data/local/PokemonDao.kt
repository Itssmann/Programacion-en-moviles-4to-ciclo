package com.abad.pokedex.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
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

    @Query("SELECT * FROM pokemones WHERE nombre = :nombre LIMIT 1")
    fun observarPorNombre(nombre: String): Flow<PokemonEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarNuevos(pokemones: List<PokemonEntity>)

    @Query("""
        UPDATE pokemones SET imagenUrl = :imagenUrl, tipos = :tipos,
        altura = :altura, peso = :peso, habilidades = :habilidades,
        estadisticas = :estadisticas WHERE nombre = :nombre
    """)
    suspend fun actualizarDatosRemotos(
        nombre: String, imagenUrl: String?, tipos: String,
        altura: Int, peso: Int, habilidades: String, estadisticas: String
    )

    // El favorito es exclusivamente local: ninguna actualización remota lo escribe.
    @Transaction
    suspend fun guardarTodos(pokemones: List<PokemonEntity>) {
        insertarNuevos(pokemones.map { it.copy(esFavorito = false) })
        pokemones.forEach {
            actualizarDatosRemotos(
                it.nombre, it.imagenUrl, it.tipos, it.altura, it.peso,
                it.habilidades, it.estadisticas
            )
        }
    }

    @Query("UPDATE pokemones SET esFavorito = NOT esFavorito WHERE nombre = :nombre")
    suspend fun alternarFavorito(nombre: String)

    @Query("SELECT COUNT(*) FROM pokemones")
    suspend fun contarTodos(): Int

    @Query("SELECT COUNT(*) FROM pokemones WHERE esFavorito = 1")
    suspend fun contarFavoritos(): Int

    @Query("DELETE FROM pokemones")
    suspend fun eliminarTodos()
}
