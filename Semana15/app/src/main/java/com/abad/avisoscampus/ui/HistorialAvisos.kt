package com.abad.avisoscampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abad.avisoscampus.model.Aviso

// No conoce el ViewModel: solo recibe la lista que debe dibujar
@Composable
fun HistorialAvisos(avisos: List<Aviso>) {
    Tarjeta("Historial") {
        if (avisos.isEmpty()) {
            Text("Sin avisos todavía", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            // Column con forEach: son pocas filas y la pantalla ya se desplaza
            avisos.take(20).forEachIndexed { indice, aviso ->
                if (indice > 0) HorizontalDivider()
                FilaAviso(aviso)
            }
        }
    }
}

@Composable
private fun FilaAviso(aviso: Aviso) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = aviso.origen.texto,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(50)
                    )
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            )
            Text(aviso.hora, style = MaterialTheme.typography.labelSmall)
        }
        Text(aviso.titulo, fontWeight = FontWeight.Bold)
        Text(aviso.texto, style = MaterialTheme.typography.bodyMedium)
    }
}
