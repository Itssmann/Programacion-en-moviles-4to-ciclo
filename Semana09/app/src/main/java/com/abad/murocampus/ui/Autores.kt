package com.abad.murocampus.ui

private val nombres = mapOf(
    1 to "Valeria Quispe",
    2 to "Diego Huamán",
    3 to "Camila Rojas",
    4 to "Mateo Flores",
    5 to "Lucía Mendoza",
    6 to "Sebastián Torres",
    7 to "Ariana Castillo",
    8 to "Joaquín Vargas",
    9 to "Fernanda Ríos",
    10 to "Renzo Paredes"
)

fun nombreDe(userId: Int): String = nombres[userId] ?: "Usuario $userId"

fun primerNombreDe(userId: Int): String = nombreDe(userId).substringBefore(" ")

fun inicialesDe(userId: Int): String =
    nombreDe(userId).split(" ").take(2).joinToString("") { it.first().uppercase() }
