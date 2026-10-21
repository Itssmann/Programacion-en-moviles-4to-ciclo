package com.abad.huellitasvet.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp

@Composable
fun GraficoBarras(
    datos: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    val maximo = (datos.maxOfOrNull { it.second } ?: 0)
        .coerceAtLeast(1)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (datos.isEmpty()) {
            Text("Todavía no hay datos para mostrar")
        }

        datos.forEach { (nombre, cantidad) ->
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        nombre,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text("$cantidad")
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                ) {
                    drawRoundRect(
                        color = Color(0xFFE2E8F0),
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                            9.dp.toPx()
                        )
                    )

                    val proporcion =
                        cantidad.coerceAtLeast(0).toFloat() / maximo

                    drawRoundRect(
                        color = Color(0xFF2563EB),
                        size = Size(
                            width = size.width * proporcion,
                            height = size.height
                        ),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                            9.dp.toPx()
                        )
                    )
                }
            }
        }
    }
}

