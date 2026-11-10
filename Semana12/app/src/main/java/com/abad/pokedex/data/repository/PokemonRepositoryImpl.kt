package com.abad.pokedex.data.repository

import com.abad.pokedex.data.remote.PokemonApiService
import com.abad.pokedex.domain.model.Pokemon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class PokemonRepositoryImpl(
    private val api: PokemonApiService
) : PokemonRepository {

    private val _pokemones =
        MutableStateFlow<List<Pokemon>>(emptyList())

    override val pokemones: Flow<List<Pokemon>> =
        _pokemones

    override val favoritos: Flow<List<Pokemon>> =
        MutableStateFlow(emptyList())

    override suspend fun refrescar(
        tipo: String
    ): Result<Int> = runCatching {

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
                    }
                )
            }

        _pokemones.value = lista
        lista.size
    }

    override suspend fun obtenerDetalle(
        nombre: String
    ): Result<Pokemon> = runCatching {

        val detalle = api.obtenerDetalle(nombre)

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

    override suspend fun alternarFavorito(nombre: String) {
        // Se implementará con Room.
    }

    override suspend fun cantidadGuardada(): Int =
        _pokemones.value.size

    override suspend fun cantidadFavoritos(): Int = 0

    override suspend fun limpiarCache() {
        _pokemones.value = emptyList()
    }
}
