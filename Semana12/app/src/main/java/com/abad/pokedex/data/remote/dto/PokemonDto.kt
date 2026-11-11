package com.abad.pokedex.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PokemonTypeResponse(
    val pokemon: List<PokemonTypeEntry>
)

data class PokemonTypeEntry(
    val pokemon: PokemonReference
)

data class PokemonReference(
    val name: String,
    val url: String
)

data class PokemonDetailDto(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: PokemonSprites,
    val types: List<PokemonTypeSlot>,
    val abilities: List<PokemonAbilitySlot>,
    val stats: List<PokemonStatSlot>
)

data class PokemonSprites(
    @SerializedName("front_default")
    val frontDefault: String?
)

data class PokemonTypeSlot(
    val type: PokemonTypeName
)

data class PokemonTypeName(
    val name: String
)

data class PokemonAbilitySlot(
    val ability: PokemonAbilityName
)

data class PokemonAbilityName(
    val name: String
)

data class PokemonStatSlot(
    @SerializedName("base_stat")
    val baseStat: Int,
    val stat: PokemonStatName
)

data class PokemonStatName(
    val name: String
)
