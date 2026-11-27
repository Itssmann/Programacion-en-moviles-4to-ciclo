package com.abad.avisoscampus.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.abad.avisoscampus.R
import com.abad.avisoscampus.model.Aviso
import com.abad.avisoscampus.ui.theme.Bordes
import com.abad.avisoscampus.ui.theme.CelesteSuave
import com.abad.avisoscampus.ui.theme.GrisAzulado

// No conoce el ViewModel: solo recibe la lista que debe dibujar
@Composable
fun HistorialAvisos(avisos: List<Aviso>) {
    val recientes = avisos.take(20)
    TarjetaSeccion(
        titulo = "Historial",
        subtitulo = if (avisos.isEmpty()) "Aquí verás cada aviso generado"
        else "Los ${recientes.size} más recientes, lo nuevo arriba",
        icono = R.drawable.ic_historial
    ) {
        Column(Modifier.animateContentSize()) {
            if (recientes.isEmpty()) {
                EstadoVacio()
            } else {
                // Column con forEach: son pocas filas y la pantalla ya se desplaza
                recientes.forEachIndexed { indice, aviso ->
                    FilaLineaTiempo(aviso, esUltimo = indice == recientes.lastIndex)
                }
            }
        }
    }
}

@Composable
private fun FilaLineaTiempo(aviso: Aviso, esUltimo: Boolean) {
    val estilo = estiloDe(aviso.origen)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Columna de la línea de tiempo: ícono del origen y la línea que baja
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconoCircular(estilo.icono, estilo.color, estilo.fondo, tamano = 32)
            if (!esUltimo) {
                Box(
                    Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(Bordes)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (esUltimo) 0.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Etiqueta(aviso.origen.texto, estilo.color, estilo.fondo)
                Spacer(Modifier.weight(1f))
                Text(
                    aviso.hora,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                aviso.titulo,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                aviso.texto,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EstadoVacio() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconoCircular(R.drawable.ic_notificacion, GrisAzulado, CelesteSuave, tamano = 56)
        Text("Sin avisos todavía", style = MaterialTheme.typography.titleSmall)
        Text(
            "Prueba el botón, el recordatorio o los receptores y los verás aquí.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
