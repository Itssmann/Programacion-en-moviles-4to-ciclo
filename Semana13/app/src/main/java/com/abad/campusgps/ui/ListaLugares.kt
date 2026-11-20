package com.abad.campusgps.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.abad.campusgps.model.LugarConDistancia
import com.abad.campusgps.util.formatearDistancia

@Composable
fun ListaLugares(
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    onSeleccionar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        lugares.forEach { item ->
            key(item.lugar.id) {
                TarjetaLugar(item, seleccionado == item.lugar.id) {
                    onSeleccionar(item.lugar.id)
                }
            }
        }
    }
}

@Composable
private fun TarjetaLugar(
    item: LugarConDistancia,
    seleccionado: Boolean,
    onSeleccionar: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    ElevatedCard(
        onClick = onSeleccionar,
        modifier = Modifier.fillMaxWidth().semantics { selected = seleccionado },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (seleccionado) colores.primaryContainer else colores.surface,
            contentColor = if (seleccionado) colores.onPrimaryContainer else colores.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(item.lugar.nombre, style = MaterialTheme.typography.titleSmall)
                Text(item.lugar.categoria, style = MaterialTheme.typography.bodySmall)
                if (seleccionado) {
                    Text("Seleccionado", style = MaterialTheme.typography.labelMedium)
                }
            }
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = colores.secondaryContainer,
                contentColor = colores.onSecondaryContainer
            ) {
                Text(
                    item.metros?.let { formatearDistancia(it) } ?: "Sin calcular",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
