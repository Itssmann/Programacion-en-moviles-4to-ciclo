package com.abad.centrotareas.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.abad.centrotareas.model.HistorialItem
import com.abad.centrotareas.model.TipoOperacion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "centrotareas_user_prefs")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val HISTORIAL_SERIALIZADO = stringPreferencesKey("historial_serializado")
        val TIEMPO_SECUENCIAL = longPreferencesKey("tiempo_secuencial")
        val TIEMPO_PARALELO = longPreferencesKey("tiempo_paralelo")
    }

    val historialFlow: Flow<List<HistorialItem>> = context.dataStore.data.map { preferences ->
        val rawString = preferences[PreferencesKeys.HISTORIAL_SERIALIZADO] ?: ""
        deserializarHistorial(rawString)
    }

    val tiempoSecuencialFlow: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.TIEMPO_SECUENCIAL]
    }

    val tiempoParaleloFlow: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.TIEMPO_PARALELO]
    }

    suspend fun agregarHistorial(item: HistorialItem) {
        context.dataStore.edit { preferences ->
            val actualRaw = preferences[PreferencesKeys.HISTORIAL_SERIALIZADO] ?: ""
            val listaActual = deserializarHistorial(actualRaw)
            val listaNueva = (listOf(item) + listaActual).take(5)
            preferences[PreferencesKeys.HISTORIAL_SERIALIZADO] = serializarHistorial(listaNueva)
        }
    }

    suspend fun guardarTiempoCarga(paralela: Boolean, milisegundos: Long) {
        context.dataStore.edit { preferences ->
            if (paralela) {
                preferences[PreferencesKeys.TIEMPO_PARALELO] = milisegundos
            } else {
                preferences[PreferencesKeys.TIEMPO_SECUENCIAL] = milisegundos
            }
        }
    }

    suspend fun limpiarHistorial() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.HISTORIAL_SERIALIZADO)
            preferences.remove(PreferencesKeys.TIEMPO_SECUENCIAL)
            preferences.remove(PreferencesKeys.TIEMPO_PARALELO)
        }
    }

    private fun serializarHistorial(items: List<HistorialItem>): String {
        return items.joinToString("\n") { item ->
            "${item.id}::${item.tipo.name}::${item.titulo}::${item.detalle}::${item.milisegundos}::${item.fechaHora}"
        }
    }

    private fun deserializarHistorial(rawString: String): List<HistorialItem> {
        if (rawString.isBlank()) return emptyList()
        return rawString.split("\n").mapNotNull { line ->
            val parts = line.split("::")
            if (parts.size >= 6) {
                try {
                    HistorialItem(
                        id = parts[0],
                        tipo = TipoOperacion.valueOf(parts[1]),
                        titulo = parts[2],
                        detalle = parts[3],
                        milisegundos = parts[4].toLong(),
                        fechaHora = parts[5]
                    )
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }
    }
}
