package com.abad.campusgps.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
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
    OpcionesFiltro(CATEGORIAS, categoriaSeleccionada, onSeleccionarCategoria, modifier)
}

@Composable
internal fun <T> OpcionesFiltro(
    opciones: List<Pair<T, String>>,
    seleccion: T,
    onSeleccionar: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    val colores = MaterialTheme.colorScheme
    Row(
        modifier = modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        opciones.forEach { (valor, etiqueta) ->
            FilterChip(
                selected = seleccion == valor,
                onClick = { onSeleccionar(valor) },
                label = { Text(etiqueta, maxLines = 1, softWrap = false) },
                shape = MaterialTheme.shapes.medium,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colores.primary,
                    selectedLabelColor = colores.onPrimary
                )
            )
        }
    }
}
