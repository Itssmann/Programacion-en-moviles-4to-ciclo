package com.abad.pokedex.model

data class Pokemon(
    val nombre: String,
    val imagenUrl: String?,
    val tipos: List<String>,
    val esFavorito: Boolean = false
)
