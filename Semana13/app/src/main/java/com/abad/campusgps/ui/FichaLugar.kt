package com.abad.campusgps.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.campusgps.model.LugarConDistancia
import com.abad.campusgps.util.formatearDistancia

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FichaLugar(
    item: LugarConDistancia,
    onCerrar: () -> Unit,
    onVerMapa: () -> Unit
) {
    val lugar = item.lugar
    val colores = MaterialTheme.colorScheme
    val tipografia = MaterialTheme.typography
    val distancia = item.metros?.let(::formatearDistancia) ?: "Sin calcular"

    ModalBottomSheet(
        onDismissRequest = onCerrar,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colores.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(lugar.categoria, color = colores.primary, style = tipografia.labelLarge)
            Text(lugar.nombre, style = tipografia.headlineSmall)
            Text("Distancia: ${distancia}", style = tipografia.labelLarge)
            Text(descripcionLugar(lugar.id), style = tipografia.bodyMedium)
            Button(onClick = onVerMapa, modifier = Modifier.fillMaxWidth()) {
                Text("Ver en el mapa")
            }
            TextButton(onClick = onCerrar, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar")
            }
        }
    }
}

private fun descripcionLugar(id: Int): String = when (id) {
    1 -> "Biblioteca central del campus y punto de referencia para el estudio."
    2 -> "Cafetería ubicada dentro del campus."
    3 -> "Laboratorio del campus dedicado a actividades de cómputo."
    4 -> "Auditorio principal del campus, identificado en la categoría de eventos."
    5 -> "Zona de estacionamiento en el sector norte del campus."
    else -> "Lugar registrado en el mapa del campus."
}
