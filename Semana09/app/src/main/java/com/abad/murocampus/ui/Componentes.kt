package com.abad.murocampus.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ColoresUsuario(val fondo: Color, val texto: Color)

@Composable
fun coloresDeUsuario(userId: Int): ColoresUsuario {
    val tono = (userId * 36f) % 360f
    val oscuro = MaterialTheme.colorScheme.background.luminance() < 0.5f
    return if (oscuro) {
        ColoresUsuario(Color.hsl(tono, 0.35f, 0.28f), Color.hsl(tono, 0.70f, 0.85f))
    } else {
        ColoresUsuario(Color.hsl(tono, 0.70f, 0.90f), Color.hsl(tono, 0.60f, 0.28f))
    }
}

@Composable
fun AvatarUsuario(userId: Int, tamano: Dp = 44.dp) {
    val colores = coloresDeUsuario(userId)
    Box(
        modifier = Modifier
            .size(tamano)
            .background(colores.fondo, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "U$userId",
            style = MaterialTheme.typography.labelLarge,
            color = colores.texto
        )
    }
}

@Composable
fun TarjetaPost(
    userId: Int,
    titulo: String,
    cuerpo: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    maxLineas: Int = 2
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarUsuario(userId, tamano = 36.dp)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Usuario $userId",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = titulo.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = cuerpo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = maxLineas,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Leer y comentar",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ListaEsqueleto(modifier: Modifier = Modifier) {
    val transicion = rememberInfiniteTransition(label = "esqueleto")
    val opacidad by transicion.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "opacidad"
    )
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .alpha(opacidad),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false
    ) {
        items(5) { TarjetaEsqueleto() }
    }
}

@Composable
private fun TarjetaEsqueleto() {
    val bloque = MaterialTheme.colorScheme.surfaceContainerHighest
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(36.dp).background(bloque, CircleShape))
                Spacer(Modifier.width(10.dp))
                Box(Modifier.width(90.dp).height(12.dp).background(bloque, CircleShape))
            }
            Spacer(Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth(0.85f).height(16.dp).background(bloque, CircleShape))
            Spacer(Modifier.height(10.dp))
            Box(Modifier.fillMaxWidth().height(12.dp).background(bloque, CircleShape))
            Spacer(Modifier.height(6.dp))
            Box(Modifier.fillMaxWidth(0.6f).height(12.dp).background(bloque, CircleShape))
        }
    }
}

@Composable
fun Cargando(texto: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(16.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EstadoVacio(
    icono: ImageVector,
    titulo: String,
    mensaje: String,
    modifier: Modifier = Modifier,
    contenedor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    tinte: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    accion: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .background(contenedor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = tinte, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text(titulo, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (accion != null) {
            Spacer(Modifier.height(24.dp))
            accion()
        }
    }
}

@Composable
fun ErrorConReintento(
    mensaje: String,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    EstadoVacio(
        icono = Icons.Filled.CloudOff,
        titulo = "No pudimos cargar",
        mensaje = mensaje,
        modifier = modifier,
        contenedor = MaterialTheme.colorScheme.errorContainer,
        tinte = MaterialTheme.colorScheme.onErrorContainer
    ) {
        Button(onClick = onReintentar, contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)) {
            Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Reintentar")
        }
    }
}
