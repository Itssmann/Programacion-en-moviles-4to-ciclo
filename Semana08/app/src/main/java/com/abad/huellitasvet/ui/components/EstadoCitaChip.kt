package com.abad.huellitasvet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EstadoCitaChip(estado: String) {
    val color = when (estado) {
        "PENDIENTE" -> Color(0xFFD97706)
        "CONFIRMADA" -> Color(0xFF16A34A)
        "ATENDIDA" -> Color(0xFF475569)
        "CANCELADA" -> Color(0xFFDC2626)
        else -> Color.Gray
    }

    Text(
        text = estado,
        color = color,
        modifier = Modifier
            .background(
                color.copy(alpha = 0.12f),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}
