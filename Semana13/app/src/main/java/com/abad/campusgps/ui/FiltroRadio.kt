package com.abad.campusgps.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
    OpcionesFiltro(RADIOS, radioSeleccionado, onSeleccionarRadio, modifier)
}
