package com.abad.pokedex.data.repository

import com.abad.pokedex.data.auth.SesionActual
import com.abad.pokedex.data.local.PokemonDao
import com.abad.pokedex.data.mapper.toDomain
import com.abad.pokedex.data.mapper.toEntity
import com.abad.pokedex.data.remote.PokemonApiService
import com.abad.pokedex.domain.model.Pokemon
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonRepositoryImpl(
    private val api: PokemonApiService,
    private val dao: PokemonDao,
    private val sesionActual: SesionActual
) : PokemonRepository {

    override val pokemones: Flow<List<Pokemon>> =
        combine(
            dao.observarTodos(),
            sesionActual.uid
        ) { entidades, uid ->
            entidades.map { entidad ->
                entidad.toDomain().copy(
                    esFavorito = false
                )
            } to uid
        }.flatMapLatest { (pokemones, uid) ->
            if (uid == null) {
                kotlinx.coroutines.flow.flowOf(pokemones)
            } else {
                dao.observarFavoritosDeUsuario(uid).map { favoritos ->
                    val nombres = favoritos.map { it.nombre }.toSet()

                    pokemones.map { pokemon ->
                        pokemon.copy(
                            esFavorito = pokemon.nombre in nombres
                        )
                    }
                }
            }
        }

    override val favoritos: Flow<List<Pokemon>> =
        sesionActual.uid.flatMapLatest { uid ->
            if (uid == null) {
                kotlinx.coroutines.flow.flowOf(emptyList())
            } else {
                dao.observarFavoritosDeUsuario(uid).map { entidades ->
                    entidades.map { entidad ->
                        entidad.toDomain().copy(esFavorito = true)
                    }
                }
            }
        }

    override fun observarDetalle(nombre: String): Flow<Pokemon?> =
        combine(
            dao.observarPorNombre(nombre),
            sesionActual.uid
        ) { entidad, uid ->
            entidad to uid
        }.flatMapLatest { (entidad, uid) ->
            if (entidad == null) {
                kotlinx.coroutines.flow.flowOf(null)
            } else if (uid == null) {
                kotlinx.coroutines.flow.flowOf(
                    entidad.toDomain().copy(esFavorito = false)
                )
            } else {
                dao.observarFavoritosDeUsuario(uid).map { favoritos ->
                    entidad.toDomain().copy(
                        esFavorito = favoritos.any {
                            it.nombre == nombre
                        }
                    )
                }
            }
        }

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

            val guardado = requireNotNull(
                dao.obtenerPorNombre(pokemon.nombre)
            )

            val uid = sesionActual.uid.value

            val esFavorito = uid != null &&
                    dao.esFavoritoDeUsuario(uid, pokemon.nombre)

            Result.success(
                guardado.toDomain().copy(esFavorito = esFavorito)
            )

        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            val guardado = dao.obtenerPorNombre(nombre)

            if (guardado != null) {
                val uid = sesionActual.uid.value

                val esFavorito = uid != null &&
                        dao.esFavoritoDeUsuario(uid, nombre)

                Result.success(
                    guardado.toDomain().copy(
                        esFavorito = esFavorito
                    )
                )
            } else {
                Result.failure(error)
            }
        }
    }

    override suspend fun alternarFavorito(nombre: String) {
        val uid = sesionActual.uid.value ?: return

        dao.alternarFavoritoDeUsuario(
            uid = uid,
            nombre = nombre
        )
    }

    override suspend fun cantidadGuardada(): Int =
        dao.contarTodos()

    override suspend fun cantidadFavoritos(): Int {
        val uid = sesionActual.uid.value ?: return 0

        return dao.contarFavoritosDeUsuario(uid)
    }

    override suspend fun limpiarCache() {
        dao.eliminarTodos()
    }

    override suspend fun sincronizarCatalogo(): Result<Int> {
        return try {
            val respuesta = api.obtenerListado(
                limite = 60,
                offset = 0
            )

            val lista = respuesta.results.map { entrada ->
                val detalle = api.obtenerDetalle(entrada.name)

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

            dao.guardarTodos(
                lista.map { it.toEntity() }
            )

            Result.success(lista.size)

        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

}
