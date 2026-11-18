package com.abad.centrotareas.data

import kotlinx.coroutines.flow.Flow

data class PrimosProgress(
    val procesados: Int,
    val limite: Int,
    val cantidadPrimos: Int,
    val porcentaje: Float
)

interface TareasRepository {
    suspend fun contarPrimos(hasta: Int): Int
    fun contarPrimosFlow(hasta: Int): Flow<PrimosProgress>
    suspend fun cargarPerfil(): String
    suspend fun cargarPedidos(simularError: Boolean): List<String>
}

