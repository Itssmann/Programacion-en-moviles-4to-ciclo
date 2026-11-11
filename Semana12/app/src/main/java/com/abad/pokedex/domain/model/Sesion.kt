package com.abad.pokedex.domain.model

enum class Rol {
    USUARIO,
    ADMIN
}

data class Sesion(
    val uid: String,
    val correo: String,
    val rol: Rol = Rol.USUARIO
)
