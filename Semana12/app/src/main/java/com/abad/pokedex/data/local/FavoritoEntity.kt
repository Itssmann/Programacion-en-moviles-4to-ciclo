package com.abad.pokedex.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "favoritos_usuario",
    primaryKeys = ["uid", "pokemonNombre"],
    foreignKeys = [
        ForeignKey(
            entity = PokemonEntity::class,
            parentColumns = ["nombre"],
            childColumns = ["pokemonNombre"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("pokemonNombre")]
)
data class FavoritoEntity(
    val uid: String,
    val pokemonNombre: String
)
