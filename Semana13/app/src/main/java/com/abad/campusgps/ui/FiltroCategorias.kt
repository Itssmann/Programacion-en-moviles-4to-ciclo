package com.abad.campusgps.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val CATEGORIAS = listOf(
    null to "Todos",
    "Estudio" to "Estudio",
    "Comida" to "Comida",
    "Eventos" to "Eventos",
    "Servicios" to "Servicios"
)

@Composable
fun FiltroCategorias(
    categoriaSeleccionada: String?,
    onSeleccionarCategoria: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = CATEGORIAS,
            key = { it.second }
        ) { (valor, etiqueta) ->
            val estaSeleccionado = categoriaSeleccionada == valor

            FilterChip(
                selected = estaSeleccionado,
                onClick = { onSeleccionarCategoria(valor) },
                label = { Text(etiqueta) }
            )
        }
    }
}
