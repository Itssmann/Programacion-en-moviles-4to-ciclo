package com.abad.saludpluscitas.data.model

data class Medico(
    val id: Int,
    val nombre: String,
    val especialidadId: Int,
    val experiencia: Int,
    val calificacion: Double
)