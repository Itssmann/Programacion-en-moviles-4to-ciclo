package com.abad.pokedex.data.auth

import com.abad.pokedex.domain.model.Rol
import com.abad.pokedex.domain.model.Sesion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    override val usuarioActual: Sesion?
        get() = firebaseAuth.currentUser?.aSesion()

    override suspend fun iniciarSesion(
        correo: String,
        contrasena: String
    ): Result<Sesion> {
        return try {
            val resultado = firebaseAuth
                .signInWithEmailAndPassword(correo, contrasena)
                .await()

            val usuario = resultado.user
                ?: error("No se pudo obtener el usuario.")

            Result.success(usuario.aSesion())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registrar(
        nombre: String,
        correo: String,
        contrasena: String
    ): Result<Sesion> {
        return try {
            val resultado = firebaseAuth
                .createUserWithEmailAndPassword(correo, contrasena)
                .await()

            val usuario = resultado.user
                ?: error("No se pudo crear la cuenta.")

            val perfil = UserProfileChangeRequest.Builder()
                .setDisplayName(nombre.trim())
                .build()

            usuario.updateProfile(perfil).await()

            Result.success(usuario.aSesion())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun cerrarSesion() {
        firebaseAuth.signOut()
    }

    private fun FirebaseUser.aSesion(): Sesion {
        return Sesion(
            uid = uid,
            correo = email.orEmpty(),
            rol = Rol.USUARIO
        )
    }
}
