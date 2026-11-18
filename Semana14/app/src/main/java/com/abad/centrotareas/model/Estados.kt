package com.abad.centrotareas.model

sealed interface PrimosUi {
    data object Inicial : PrimosUi
    data class Calculando(
        val procesados: Int = 0,
        val limite: Int = 0,
        val cantidad: Int = 0,
        val progreso: Float = 0f
    ) : PrimosUi
    data class Listo(
        val cantidad: Int,
        val milisegundos: Long
    ) : PrimosUi
    data object Cancelado : PrimosUi
}

sealed interface CargaUi {
    data object Inicial : CargaUi
    data object Cargando : CargaUi
    data class Exito(
        val modo: String,
        val perfil: String,
        val pedidos: Int,
        val milisegundos: Long
    ) : CargaUi
    data class Error(val mensaje: String) : CargaUi
}
