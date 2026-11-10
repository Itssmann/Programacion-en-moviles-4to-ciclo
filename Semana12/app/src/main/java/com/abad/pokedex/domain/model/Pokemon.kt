package com.abad.pokedex.domain.model

data class Pokemon(
    val nombre: String,
    val imagenUrl: String?,
    val tipos: List<String>,
    val esFavorito: Boolean = false,
    val altura: Int = 0,
    val peso: Int = 0,
    val habilidades: List<String> = emptyList(),
    val estadisticas: Map<String, Int> = emptyMap()
)
