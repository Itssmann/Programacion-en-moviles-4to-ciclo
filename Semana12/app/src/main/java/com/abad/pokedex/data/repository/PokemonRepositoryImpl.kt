package com.abad.pokedex.data.repository

import com.abad.pokedex.data.local.PokemonDao
import com.abad.pokedex.data.mapper.toDomain
import com.abad.pokedex.data.mapper.toEntity
import com.abad.pokedex.data.remote.PokemonApiService
import com.abad.pokedex.domain.model.Pokemon
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PokemonRepositoryImpl(
    private val api: PokemonApiService,
    private val dao: PokemonDao
) : PokemonRepository {

    override val pokemones: Flow<List<Pokemon>> =
        dao.observarTodos().map { entidades ->
            entidades.map { it.toDomain() }
        }

    override val favoritos: Flow<List<Pokemon>> =
        dao.observarFavoritos().map { entidades ->
            entidades.map { it.toDomain() }
        }

    override fun observarDetalle(nombre: String): Flow<Pokemon?> =
        dao.observarPorNombre(nombre).map { it?.toDomain() }

    override suspend fun refrescar(tipo: String): Result<Int> {
        return try {
            val respuesta = api.obtenerPorTipo(tipo)

            val lista = respuesta.pokemon.take(20).map { entrada ->
                val detalle = api.obtenerDetalle(entrada.pokemon.name)

                Pokemon(
                    nombre = detalle.name,
                    imagenUrl = detalle.sprites.frontDefault,
                    tipos = detalle.types.map { it.type.name },
                    altura = detalle.height,
                    peso = detalle.weight,
                    habilidades = detalle.abilities.map {
                        it.ability.name
                    },
                    estadisticas = detalle.stats.associate {
                        it.stat.name to it.baseStat
                    }
                )
            }

            dao.guardarTodos(lista.map { it.toEntity() })
            Result.success(lista.size)

        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    override suspend fun obtenerDetalle(
        nombre: String
    ): Result<Pokemon> {
        return try {
            val detalle = api.obtenerDetalle(nombre)

            val pokemon = Pokemon(
                nombre = detalle.name,
                imagenUrl = detalle.sprites.frontDefault,
                tipos = detalle.types.map { it.type.name },
                altura = detalle.height,
                peso = detalle.weight,
                habilidades = detalle.abilities.map {
                    it.ability.name
                },
                estadisticas = detalle.stats.associate {
                    it.stat.name to it.baseStat
                }
            )

            dao.guardarTodos(listOf(pokemon.toEntity()))
            Result.success(requireNotNull(dao.obtenerPorNombre(pokemon.nombre)).toDomain())

        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            val guardado = dao.obtenerPorNombre(nombre)
            if (guardado != null) {
                Result.success(guardado.toDomain())
            } else {
                Result.failure(error)
            }
        }
    }

    override suspend fun alternarFavorito(nombre: String) {
        dao.alternarFavorito(nombre)
    }

    override suspend fun cantidadGuardada(): Int =
        dao.contarTodos()

    override suspend fun cantidadFavoritos(): Int =
        dao.contarFavoritos()

    override suspend fun limpiarCache() {
        dao.eliminarTodos()
    }
}
