package com.abad.huellitasvet.ui.components

import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun FechaChip(
    fecha: LocalDate,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val formato = DateTimeFormatter.ofPattern(
        "EEE dd",
        Locale("es", "PE")
    )

    FilterChip(
        selected = seleccionado,
        onClick = onClick,
        label = { Text(fecha.format(formato)) }
    )
}

