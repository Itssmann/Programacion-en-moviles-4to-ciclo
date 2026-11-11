package com.abad.pokedex.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SesionActual(
    private val firebaseAuth: FirebaseAuth? = null,
    uidInicial: String? = null
) {
    private val _uid = MutableStateFlow(
        firebaseAuth?.currentUser?.uid ?: uidInicial
    )

    val uid: StateFlow<String?> = _uid.asStateFlow()

    private val listener = AuthStateListener { auth ->
        _uid.value = auth.currentUser?.uid
    }

    init {
        firebaseAuth?.addAuthStateListener(listener)
    }

    fun cambiarUsuarioDePrueba(uid: String?) {
        require(firebaseAuth == null) {
            "No se puede simular una sesión de Firebase real"
        }
        _uid.value = uid
    }

    fun cerrar() {
        firebaseAuth?.removeAuthStateListener(listener)
    }

    companion object {
        fun firebase(): SesionActual =
            SesionActual(firebaseAuth = FirebaseAuth.getInstance())

        fun paraPruebas(uid: String = "usuario-prueba"): SesionActual =
            SesionActual(uidInicial = uid)
    }
}
