package com.abad.saludpluscitas.data.model

data class Cita(
    val id: Int,
    val usuarioId: Int,
    val medicoId: Int,
    val fecha: String,
    val hora: String,
    val estado: String,
    val sede: String = "Sede Principal (Jesús María)"
)
