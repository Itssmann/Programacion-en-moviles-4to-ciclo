package com.abad.pokedex.data.remote.dto

data class PokemonListResponse(
    val results: List<PokemonListItemDto>
)

data class PokemonListItemDto(
    val name: String,
    val url: String
)
