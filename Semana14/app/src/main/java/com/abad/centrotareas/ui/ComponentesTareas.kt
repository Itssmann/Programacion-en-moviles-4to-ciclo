package com.abad.centrotareas.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.abad.centrotareas.ui.theme.Tono
import com.abad.centrotareas.ui.theme.coloresEstado

internal enum class Simbolo { TAREA, PRIMOS, CARGAS, GRAFICO, HISTORIAL, EXITO, CANCELADO, ERROR }

// Iconos vectoriales de 24 unidades, sin añadir una biblioteca de iconos.
// Son decorativos: los títulos y botones contiguos proporcionan el nombre accesible.
@Composable
internal fun Icono(simbolo: Simbolo, color: Color = MaterialTheme.colorScheme.primary) {
    Canvas(Modifier.size(24.dp)) {
        withTransform({ scale(size.width / 24f, size.height / 24f, Offset.Zero) }) {
            fun linea(x: Float, y: Float, xx: Float, yy: Float) =
                drawLine(color, Offset(x, y), Offset(xx, yy), 1.8f, StrokeCap.Round)
            when (simbolo) {
                Simbolo.TAREA -> {
                    drawCircle(color, 9f, Offset(12f, 12f), style = Stroke(1.8f))
                    linea(12f, 6f, 12f, 12f); linea(12f, 12f, 16f, 14f)
                }
                Simbolo.PRIMOS -> {
                    linea(9f, 4f, 7f, 20f); linea(17f, 4f, 15f, 20f)
                    linea(4f, 9f, 21f, 9f); linea(3f, 15f, 20f, 15f)
                }
                Simbolo.CARGAS -> {
                    linea(4f, 8f, 20f, 8f); linea(16f, 4f, 20f, 8f); linea(16f, 12f, 20f, 8f)
                    linea(20f, 16f, 4f, 16f); linea(8f, 12f, 4f, 16f); linea(8f, 20f, 4f, 16f)
                }
                Simbolo.GRAFICO -> {
                    linea(4f, 5f, 4f, 20f); linea(4f, 20f, 21f, 20f)
                    linea(8f, 14f, 8f, 16f); linea(13f, 9f, 13f, 16f); linea(18f, 4f, 18f, 16f)
                }
                Simbolo.HISTORIAL -> {
                    drawArc(color, -140f, 290f, false, Offset(4f, 4f), androidx.compose.ui.geometry.Size(16f, 16f), style = Stroke(1.8f))
                    linea(3f, 3f, 3f, 9f); linea(3f, 9f, 8f, 9f)
                    linea(12f, 7f, 12f, 12f); linea(12f, 12f, 16f, 14f)
                }
                Simbolo.EXITO -> { linea(5f, 12f, 10f, 17f); linea(10f, 17f, 20f, 7f) }
                Simbolo.CANCELADO -> {
                    drawCircle(color, 9f, Offset(12f, 12f), style = Stroke(1.8f))
                    linea(8f, 8f, 16f, 16f); linea(16f, 8f, 8f, 16f)
                }
                Simbolo.ERROR -> {
                    drawCircle(color, 9f, Offset(12f, 12f), style = Stroke(1.8f))
                    linea(12f, 6f, 12f, 13f); drawCircle(color, 1f, Offset(12f, 17f))
                }
            }
        }
    }
}

@Composable
internal fun Seccion(titulo: String, descripcion: String, icono: Simbolo, content: @Composable ColumnScope.() -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.medium) {
                    Box(Modifier.padding(12.dp)) { Icono(icono, MaterialTheme.colorScheme.onSecondaryContainer) }
                }
                Column(Modifier.weight(1f)) {
                    Text(titulo, style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading() })
                    Text(descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            content()
        }
    }
}

@Composable
internal fun Etiqueta(texto: String, tono: Tono) {
    val (fondo, tinta) = coloresEstado(tono)
    Surface(color = fondo, contentColor = tinta, shape = MaterialTheme.shapes.small) {
        Row(Modifier.padding(horizontal = 10.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icono(when (tono) {
                Tono.EXITO -> Simbolo.EXITO
                Tono.CANCELADO -> Simbolo.CANCELADO
                Tono.ERROR -> Simbolo.ERROR
                else -> Simbolo.TAREA
            }, tinta)
            Text(texto, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
internal fun Aviso(titulo: String, detalle: String, tono: Tono) {
    val (fondo, tinta) = coloresEstado(tono)
    Surface(color = fondo, contentColor = tinta, shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleSmall)
            Text(detalle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
internal fun Progreso(valor: Float, tono: Tono = Tono.ACTIVO, animar: Boolean = true) {
    val animado by animateFloatAsState(valor, tween(if (animar) 180 else 0), label = "Progreso visual")
    val color = when (tono) {
        Tono.ACTIVO -> MaterialTheme.colorScheme.secondary
        else -> coloresEstado(tono).second
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("${(valor * 100).toInt()}%", style = MaterialTheme.typography.displaySmall, color = color)
        LinearProgressIndicator(
            progress = { if (animar) animado else valor },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = color, trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
internal fun Metrica(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = MaterialTheme.colorScheme.surfaceContainerLow, shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valor, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun Acciones(content: @Composable FlowRowScope.() -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp), content = content)
}
