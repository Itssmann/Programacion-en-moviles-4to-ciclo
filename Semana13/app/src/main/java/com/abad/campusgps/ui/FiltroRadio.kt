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

private val RADIOS = listOf(
    null to "Sin límite",
    100 to "100 m",
    200 to "200 m",
    500 to "500 m"
)

@Composable
fun FiltroRadio(
    radioSeleccionado: Int?,
    onSeleccionarRadio: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = RADIOS,
            key = { it.second }
        ) { (valor, etiqueta) ->
            val estaSeleccionado = radioSeleccionado == valor

            FilterChip(
                selected = estaSeleccionado,
                onClick = { onSeleccionarRadio(valor) },
                label = { Text(etiqueta) }
            )
        }
    }
}
