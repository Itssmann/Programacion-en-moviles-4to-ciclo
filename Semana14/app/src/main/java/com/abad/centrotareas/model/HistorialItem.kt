package com.abad.centrotareas.model

enum class TipoOperacion {
    PRIMOS,
    CARGA_SECUENCIAL,
    CARGA_PARALELA
}

enum class FiltroHistorial {
    TODOS,
    PRIMOS,
    CARGAS
}

data class HistorialItem(
    val id: String,
    val tipo: TipoOperacion,
    val titulo: String,
    val detalle: String,
    val milisegundos: Long,
    val fechaHora: String
)
