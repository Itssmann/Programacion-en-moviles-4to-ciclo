package com.abad.lab03registroproducto

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

@Composable
fun PantallaNotas(modifier: Modifier = Modifier) {
    var notaFundamentos by remember { mutableStateOf(0f) }
    var notaPOO by remember { mutableStateOf(0f) }
    var notaMoviles by remember { mutableStateOf(0f) }
    var notaBaseDatos by remember { mutableStateOf(0f) }
    var redondear by remember { mutableStateOf(false) }
    var confirmado by remember { mutableStateOf(false) }
    var mostrarResultado by remember { mutableStateOf(false) }

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

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("Redondear promedio final")
            Switch(checked = redondear, onCheckedChange = { redondear = it })
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = confirmado, onCheckedChange = { confirmado = it })
            Text("Confirmo que las notas son correctas")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { mostrarResultado = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = confirmado
        ) {
            Text("CALCULAR PROMEDIO")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (mostrarResultado && confirmado) {
            val promedioPonderado = notaFundamentos.roundToInt() * 0.20 +
                    notaPOO.roundToInt() * 0.25 +
                    notaMoviles.roundToInt() * 0.30 +
                    notaBaseDatos.roundToInt() * 0.25

            val promedioFinal = if (redondear) promedioPonderado.roundToInt().toDouble() else promedioPonderado
            val aprobado = promedioFinal >= 10.5

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Promedio ponderado: " + String.format("%.2f", promedioPonderado))
                    Text(
                        "Promedio final: " + String.format("%.0f", promedioFinal),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    if (redondear) {
                        Text("(redondeado)", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (aprobado) "APROBADO" else "DESAPROBADO",
                        color = if (aprobado) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("✓ Promedio calculado correctamente", color = Color(0xFF2E7D32))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Desarrollado por: Abad Anchiraico Luis Pablo",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}
@Preview(showBackground = true)
@Composable
fun PantallaNotasPreview() {
    PantallaNotas()
}