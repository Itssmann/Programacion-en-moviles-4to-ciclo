package com.abad.pokedex.data.remote

import com.abad.pokedex.data.remote.dto.PokemonDetailDto
import com.abad.pokedex.data.remote.dto.PokemonTypeResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonApiService {

    @GET("type/{tipo}")
    suspend fun obtenerPorTipo(
        @Path("tipo") tipo: String
    ): PokemonTypeResponse

    @GET("pokemon/{nombre}")
    suspend fun obtenerDetalle(
        @Path("nombre") nombre: String
    ): PokemonDetailDto
}
