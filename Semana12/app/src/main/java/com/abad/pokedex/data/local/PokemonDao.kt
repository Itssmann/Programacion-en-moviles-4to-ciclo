package com.abad.pokedex.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    @Query("SELECT * FROM pokemones ORDER BY nombre ASC")
    fun observarTodos(): Flow<List<PokemonEntity>>

    @Query("""
        SELECT * FROM pokemones
        WHERE esFavorito = 1
        ORDER BY nombre ASC
    """)
    fun observarFavoritos(): Flow<List<PokemonEntity>>

    @Query("""
        SELECT p.* FROM pokemones p
        INNER JOIN favoritos_usuario f
            ON p.nombre = f.pokemonNombre
        WHERE f.uid = :uid
        ORDER BY p.nombre ASC
    """)
    fun observarFavoritosDeUsuario(
        uid: String
    ): Flow<List<PokemonEntity>>

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM favoritos_usuario
            WHERE uid = :uid AND pokemonNombre = :nombre
        )
    """)
    suspend fun esFavoritoDeUsuario(
        uid: String,
        nombre: String
    ): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarFavorito(
        favorito: FavoritoEntity
    )

    @Query("""
        DELETE FROM favoritos_usuario
        WHERE uid = :uid AND pokemonNombre = :nombre
    """)
    suspend fun eliminarFavorito(
        uid: String,
        nombre: String
    )

    @Transaction
    suspend fun alternarFavoritoDeUsuario(
        uid: String,
        nombre: String
    ) {
        if (esFavoritoDeUsuario(uid, nombre)) {
            eliminarFavorito(uid, nombre)
        } else {
            insertarFavorito(
                FavoritoEntity(
                    uid = uid,
                    pokemonNombre = nombre
                )
            )
        }
    }

    @Query("""
        SELECT * FROM pokemones
        WHERE nombre = :nombre LIMIT 1
    """)
    suspend fun obtenerPorNombre(
        nombre: String
    ): PokemonEntity?

    @Query("""
        SELECT * FROM pokemones
        WHERE nombre = :nombre LIMIT 1
    """)
    fun observarPorNombre(
        nombre: String
    ): Flow<PokemonEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarNuevos(
        pokemones: List<PokemonEntity>
    )

    @Query("""
        UPDATE pokemones
        SET imagenUrl = :imagenUrl,
            tipos = :tipos,
            altura = :altura,
            peso = :peso,
            habilidades = :habilidades,
            estadisticas = :estadisticas
        WHERE nombre = :nombre
    """)
    suspend fun actualizarDatosRemotos(
        nombre: String,
        imagenUrl: String?,
        tipos: String,
        altura: Int,
        peso: Int,
        habilidades: String,
        estadisticas: String
    )

    @Transaction
    suspend fun guardarTodos(
        pokemones: List<PokemonEntity>
    ) {
        insertarNuevos(
            pokemones.map {
                it.copy(esFavorito = false)
            }
        )

        pokemones.forEach {
            actualizarDatosRemotos(
                it.nombre,
                it.imagenUrl,
                it.tipos,
                it.altura,
                it.peso,
                it.habilidades,
                it.estadisticas
            )
        }
    }

    // Métodos anteriores, conservados temporalmente.

    @Query("""
        UPDATE pokemones
        SET esFavorito = NOT esFavorito
        WHERE nombre = :nombre
    """)
    suspend fun alternarFavorito(nombre: String)

    @Query("SELECT COUNT(*) FROM pokemones")
    suspend fun contarTodos(): Int

    @Query("""
        SELECT COUNT(*) FROM pokemones
        WHERE esFavorito = 1
    """)
    suspend fun contarFavoritos(): Int

    @Query("DELETE FROM pokemones")
    suspend fun eliminarTodos()

    @Query("""
    SELECT COUNT(*) FROM favoritos_usuario
    WHERE uid = :uid
""")
    suspend fun contarFavoritosDeUsuario(uid: String): Int
}
