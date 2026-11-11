package com.abad.pokedex.data.auth

import com.abad.pokedex.domain.model.Sesion

interface AuthRepository {

    val usuarioActual: Sesion?

    suspend fun iniciarSesion(
        correo: String,
        contrasena: String
    ): Result<Sesion>

    suspend fun registrar(
        nombre: String,
        correo: String,
        contrasena: String
    ): Result<Sesion>

    fun cerrarSesion()
}
