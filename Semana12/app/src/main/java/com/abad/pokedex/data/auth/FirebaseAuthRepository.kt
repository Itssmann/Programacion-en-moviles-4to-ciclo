package com.abad.pokedex.data.auth

import com.abad.pokedex.domain.model.Rol
import com.abad.pokedex.domain.model.Sesion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    override val usuarioActual: Sesion?
        get() = firebaseAuth.currentUser?.let { usuario ->
            Sesion(
                uid = usuario.uid,
                correo = usuario.email.orEmpty(),
                rol = Rol.USUARIO
            )
        }

    override suspend fun obtenerSesionActualizada(): Sesion? {
        val usuario = firebaseAuth.currentUser ?: return null
        return usuario.obtenerSesion()
    }

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

            Result.success(usuario.obtenerSesion())

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

            Result.success(usuario.obtenerSesion())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun cerrarSesion() {
        firebaseAuth.signOut()
    }

    private suspend fun FirebaseUser.obtenerSesion(): Sesion {
        val documento = firestore
            .collection("usuarios")
            .document(uid)
            .get()
            .await()

        val rolFirestore = documento.getString("rol")
            ?.trim()
            ?.lowercase()

        val rol = when (rolFirestore) {
            "admin" -> Rol.ADMIN
            else -> Rol.USUARIO
        }

        android.util.Log.d(
            "POKEDEX_ROL",
            "Rol recibido: '$rolFirestore', rol asignado: $rol"
        )

        android.util.Log.d(
            "POKEDEX_ROL",
            "Documento existe: ${documento.exists()}, rol: ${documento.getString("rol")}, rol asignado: $rol"
        )

        return Sesion(
            uid = uid,
            correo = email.orEmpty(),
            rol = rol
        )
    }
}
