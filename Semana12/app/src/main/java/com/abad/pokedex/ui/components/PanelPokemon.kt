package com.abad.pokedex.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HeroPokemon(colores: List<Color>, content: @Composable ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Brush.linearGradient(colores))) {
        Canvas(Modifier.matchParentSize()) {
            val radio = size.width * 0.36f
            val centro = Offset(size.width * 0.94f, size.height * 0.72f)
            val tinta = Color.White.copy(alpha = 0.09f)
            drawCircle(tinta, radio, centro, style = Stroke(10.dp.toPx()))
            drawLine(tinta, centro - Offset(radio, 0f), centro + Offset(radio, 0f), 10.dp.toPx())
            drawCircle(colores.last(), radio * 0.23f, centro)
            drawCircle(tinta, radio * 0.23f, centro, style = Stroke(9.dp.toPx()))
            drawCircle(tinta, radio * 1.32f, centro, style = Stroke(1.dp.toPx()))
        }
        Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            content()
        }
    }
}

@Composable
fun TituloSeccion(titulo: String, descripcion: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(descripcion, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun EstadisticasColeccion(total: Int?, favoritos: Int?) {
    val fuenteGrande = LocalDensity.current.fontScale > 1.35f
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        if (maxWidth < 310.dp || fuenteGrande) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DatoColeccion("Pokémon disponibles", total, Icons.Default.CatchingPokemon, false, Modifier.fillMaxWidth())
                DatoColeccion("Mis favoritos", favoritos, Icons.Default.Favorite, true, Modifier.fillMaxWidth())
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DatoColeccion("Pokémon disponibles", total, Icons.Default.CatchingPokemon, false, Modifier.weight(1.15f))
                DatoColeccion("Mis favoritos", favoritos, Icons.Default.Favorite, true, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DatoColeccion(titulo: String, valor: Int?, icono: ImageVector, favorito: Boolean, modifier: Modifier) {
    val fondo = if (favorito) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val tinta = if (favorito) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
    Card(modifier.animateContentSize(), colors = CardDefaults.cardColors(containerColor = fondo, contentColor = tinta),
        shape = RoundedCornerShape(22.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icono, contentDescription = null, modifier = Modifier.size(26.dp))
            Text(valor?.toString() ?: "—", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold)
            Text(titulo, style = MaterialTheme.typography.labelLarge)
        }
    }
}
