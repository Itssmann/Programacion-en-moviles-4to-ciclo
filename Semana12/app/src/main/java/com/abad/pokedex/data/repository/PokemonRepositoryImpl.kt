package com.abad.pokedex.data.repository

import com.abad.pokedex.data.local.PokemonDao
import com.abad.pokedex.data.mapper.toDomain
import com.abad.pokedex.data.mapper.toEntity
import com.abad.pokedex.data.remote.PokemonApiService
import com.abad.pokedex.domain.model.Pokemon
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PokemonRepositoryImpl(
    private val api: PokemonApiService,
    private val dao: PokemonDao
) : PokemonRepository {

    private val _pokemones =
        MutableStateFlow<List<Pokemon>>(emptyList())

    override val pokemones: Flow<List<Pokemon>> = _pokemones

    override val favoritos: Flow<List<Pokemon>> =
        dao.observarFavoritos().map { lista ->
            lista.map { it.toDomain() }
        }

    override suspend fun refrescar(
        tipo: String
    ): Result<Int> {
        return try {
            val respuesta = api.obtenerPorTipo(tipo)

            val lista = respuesta.pokemon
                .take(20)
                .map { entrada ->
                    val detalle = api.obtenerDetalle(
                        entrada.pokemon.name
                    )

                    Pokemon(
                        nombre = detalle.name,
                        imagenUrl = detalle.sprites.frontDefault,
                        tipos = detalle.types.map {
                            it.type.name
                        },
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

            val conservados = lista.map { pokemon ->
                val anterior = dao.obtenerPorNombre(
                    pokemon.nombre
                )

                pokemon.copy(
                    esFavorito = anterior?.esFavorito ?: false
                )
            }

            dao.guardarTodos(
                conservados.map { it.toEntity() }
            )

            _pokemones.value = conservados

            Result.success(conservados.size)

        } catch (error: CancellationException) {
            throw error

        } catch (error: Exception) {
            val guardados: List<Pokemon> =
                dao.observarTodos()
                    .first()
                    .map { it.toDomain() }
                    .filter { tipo in it.tipos }

            if (guardados.isNotEmpty()) {
                _pokemones.value = guardados
                Result.success(guardados.size)
            } else {
                Result.failure(error)
            }
        }
    }

    override suspend fun obtenerDetalle(
        nombre: String
    ): Result<Pokemon> {
        return try {
            val detalle = api.obtenerDetalle(nombre)

            val anterior = dao.obtenerPorNombre(nombre)

            val pokemon = Pokemon(
                nombre = detalle.name,
                imagenUrl = detalle.sprites.frontDefault,
                tipos = detalle.types.map {
                    it.type.name
                },
                esFavorito = anterior?.esFavorito ?: false,
                altura = detalle.height,
                peso = detalle.weight,
                habilidades = detalle.abilities.map {
                    it.ability.name
                },
                estadisticas = detalle.stats.associate {
                    it.stat.name to it.baseStat
                }
            )

            dao.guardarTodos(
                listOf(pokemon.toEntity())
            )

            Result.success(pokemon)

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
        val actual = dao.obtenerPorNombre(nombre) ?: return

        dao.actualizarFavorito(
            nombre = nombre,
            favorito = !actual.esFavorito
        )
    }

    override suspend fun cantidadGuardada(): Int =
        dao.contarTodos()

    override suspend fun cantidadFavoritos(): Int =
        dao.contarFavoritos()

    override suspend fun limpiarCache() {
        dao.eliminarTodos()
        _pokemones.value = emptyList()
    }
}
