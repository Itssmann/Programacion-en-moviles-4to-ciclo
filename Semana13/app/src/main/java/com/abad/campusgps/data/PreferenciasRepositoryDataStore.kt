package com.abad.campusgps.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "preferencias_campus")

class PreferenciasRepositoryDataStore(
    context: Context
) : PreferenciasRepository {

    private val contexto = context.applicationContext

    private object PreferencesKeys {
        val LUGAR_SELECCIONADO = intPreferencesKey("lugar_seleccionado")
    }

    override val lugarSeleccionado: Flow<Int?> =
        contexto.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.LUGAR_SELECCIONADO]
        }

    override suspend fun guardarLugarSeleccionado(id: Int?) {
        contexto.dataStore.edit { preferences ->
            if (id == null) {
                preferences.remove(PreferencesKeys.LUGAR_SELECCIONADO)
            } else {
                preferences[PreferencesKeys.LUGAR_SELECCIONADO] = id
            }
        }
    }
}
