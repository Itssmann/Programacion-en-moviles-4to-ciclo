package com.abad.pokedex.data.local

import androidx.room.Entity

@Entity(
    tableName = "favoritos_usuario",
    primaryKeys = ["uid", "pokemonNombre"]
)
data class FavoritoEntity(
    val uid: String,
    val pokemonNombre: String
)
