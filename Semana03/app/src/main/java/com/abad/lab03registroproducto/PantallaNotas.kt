package com.abad.lab03registroproducto

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun PantallaNotas(modifier: Modifier = Modifier) {
    var notaFundamentos by remember { mutableStateOf(0f) }
    var notaPOO by remember { mutableStateOf(0f) }
    var notaMoviles by remember { mutableStateOf(0f) }
    var notaBaseDatos by remember { mutableStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Notas del ciclo", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Desliza para asignar cada nota (0 a 20)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Fundamentos de Programación (20%)")
            Text("${notaFundamentos.roundToInt()}")
        }
        Slider(
            value = notaFundamentos,
            onValueChange = { notaFundamentos = it },
            valueRange = 0f..20f
        )

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Programación Orientada a Objetos (25%)")
            Text("${notaPOO.roundToInt()}")
        }
        Slider(
            value = notaPOO,
            onValueChange = { notaPOO = it },
            valueRange = 0f..20f
        )

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Programación en Móviles (30%)")
            Text("${notaMoviles.roundToInt()}")
        }
        Slider(
            value = notaMoviles,
            onValueChange = { notaMoviles = it },
            valueRange = 0f..20f
        )

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Base de Datos (25%)")
            Text("${notaBaseDatos.roundToInt()}")
        }
        Slider(
            value = notaBaseDatos,
            onValueChange = { notaBaseDatos = it },
            valueRange = 0f..20f)
    }
}
@Preview(showBackground = true)
@Composable
fun PantallaNotasPreview() {
    PantallaNotas()
}