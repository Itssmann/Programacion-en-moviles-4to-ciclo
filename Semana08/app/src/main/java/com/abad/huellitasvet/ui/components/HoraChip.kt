package com.abad.huellitasvet.ui.components

import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun HoraChip(
    hora: String,
    seleccionado: Boolean,
    disponible: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = seleccionado,
        enabled = disponible,
        onClick = onClick,
        label = { Text(hora) }
    )
}

