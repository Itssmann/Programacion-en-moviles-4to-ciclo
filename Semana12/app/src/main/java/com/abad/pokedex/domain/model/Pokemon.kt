package com.abad.pokedex.domain.model

data class Pokemon(
    val nombre: String,
    val imagenUrl: String?,
    val tipos: List<String>,
    val esFavorito: Boolean = false
)
