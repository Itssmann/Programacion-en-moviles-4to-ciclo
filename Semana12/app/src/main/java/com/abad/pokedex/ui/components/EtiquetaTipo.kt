package com.abad.pokedex.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EtiquetaTipo(tipo: String) {
    val color = when (tipo.lowercase()) {
        "fire" -> Color(0xFFE76F51)
        "water" -> Color(0xFF478BD3)
        "grass" -> Color(0xFF4BA879)
        "poison" -> Color(0xFF9B59B6)
        "ice" -> Color(0xFF48BFCB)
        "flying" -> Color(0xFF7D8FD6)
        else -> MaterialTheme.colorScheme.secondary
    }

    val nombre = when (tipo.lowercase()) {
        "fire" -> "Fuego"
        "water" -> "Agua"
        "grass" -> "Planta"
        "poison" -> "Veneno"
        "ice" -> "Hielo"
        "flying" -> "Volador"
        else -> tipo.replaceFirstChar { it.uppercase() }
    }

    Surface(
        color = color.copy(alpha = 0.14f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = nombre,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            ),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}