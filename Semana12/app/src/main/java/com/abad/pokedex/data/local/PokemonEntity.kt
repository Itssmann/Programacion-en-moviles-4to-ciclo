package com.abad.pokedex.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemones")
data class PokemonEntity(
    @PrimaryKey
    val nombre: String,
    val imagenUrl: String?,
    val tipos: String,
    val esFavorito: Boolean = false,
    val altura: Int = 0,
    val peso: Int = 0,
    val habilidades: String = "",
    val estadisticas: String = ""
)
