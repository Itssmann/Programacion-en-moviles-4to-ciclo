package com.abad.campusgps.data

import kotlinx.coroutines.flow.Flow

interface PreferenciasRepository {
    val lugarSeleccionado: Flow<Int?>
    suspend fun guardarLugarSeleccionado(id: Int?)
}
