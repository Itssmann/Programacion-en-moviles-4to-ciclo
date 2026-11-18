package com.abad.centrotareas.model

enum class TipoOperacion {
    PRIMOS,
    CARGA_SECUENCIAL,
    CARGA_PARALELA,
    TAREA_PROGRESO
}

enum class FiltroHistorial {
    TODOS,
    TAREAS,
    PRIMOS,
    CARGAS
}

enum class EstadoRegistro {
    EXITOSO,
    CANCELADO,
    FALLIDO
}

data class HistorialItem(
    val id: String,
    val tipo: TipoOperacion,
    val titulo: String,
    val detalle: String,
    val milisegundos: Long,
    val fechaHora: String,
    val esError: Boolean = false,
    val estadoRegistro: EstadoRegistro = if (esError) EstadoRegistro.FALLIDO else EstadoRegistro.EXITOSO
)
