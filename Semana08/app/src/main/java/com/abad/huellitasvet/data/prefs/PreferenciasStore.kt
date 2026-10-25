package com.abad.huellitasvet.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Declarar una sola vez, fuera de la clase.
private val Context.dataStore by preferencesDataStore(name = "ajustes")

class PreferenciasStore(private val context: Context) {

    private object Claves {
        val PERFIL_ACTIVO = stringPreferencesKey("perfilActivo")
        val USUARIO_ID = intPreferencesKey("usuarioId")
        val MODO_OSCURO = booleanPreferencesKey("modoOscuro")
        val NOTIFICACIONES = booleanPreferencesKey("notificaciones")
        val ACEPTAR_CITAS = booleanPreferencesKey("aceptarCitas")
    }

    private val preferencias = context.dataStore.data.catch { error ->
        if (error is IOException) {
            emit(emptyPreferences())
        } else {
            throw error
        }
    }

    val perfilActivo: Flow<String> = preferencias.map {
        it[Claves.PERFIL_ACTIVO] ?: ""
    }

    val usuarioId: Flow<Int> = preferencias.map {
        it[Claves.USUARIO_ID] ?: 0
    }

    val modoOscuro: Flow<Boolean> = preferencias.map {
        it[Claves.MODO_OSCURO] ?: false
    }

    val notificaciones: Flow<Boolean> = preferencias.map {
        it[Claves.NOTIFICACIONES] ?: true
    }

    val aceptarCitas: Flow<Boolean> = preferencias.map {
        it[Claves.ACEPTAR_CITAS] ?: true
    }

    suspend fun guardarSesion(rol: String, usuarioId: Int) {
        require(rol == "CLIENTE" || rol == "ADMIN") {
            "Rol no válido"
        }
        require(usuarioId > 0) {
            "ID de usuario no válido"
        }

        context.dataStore.edit {
            it[Claves.PERFIL_ACTIVO] = rol
            it[Claves.USUARIO_ID] = usuarioId
        }
    }

    suspend fun guardarModoOscuro(valor: Boolean) {
        context.dataStore.edit {
            it[Claves.MODO_OSCURO] = valor
        }
    }

    suspend fun guardarNotificaciones(valor: Boolean) {
        context.dataStore.edit {
            it[Claves.NOTIFICACIONES] = valor
        }
    }

    suspend fun guardarAceptarCitas(valor: Boolean) {
        context.dataStore.edit {
            it[Claves.ACEPTAR_CITAS] = valor
        }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit {
            it.remove(Claves.PERFIL_ACTIVO)
            it.remove(Claves.USUARIO_ID)
        }
    }
}

