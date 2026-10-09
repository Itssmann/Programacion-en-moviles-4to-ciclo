package com.abad.saludpluscitas.data.model

import com.abad.saludpluscitas.R

data class Medico(
    val id: Int,
    val nombre: String,
    val especialidadId: Int,
    val experiencia: Int,
    val calificacion: Double,
    val fotoResId: Int = R.drawable.ic_launcher_foreground
)
