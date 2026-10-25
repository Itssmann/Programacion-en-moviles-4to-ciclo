package com.abad.huellitasvet.ui.components.graficos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GraficoBarras(
    datos: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    val maximo = (datos.maxOfOrNull { it.second } ?: 0)
        .coerceAtLeast(1)

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        if (datos.isEmpty()) {
            Box(modifier = Modifier.padding(16.dp)) {
                Text("Todavía no hay datos para mostrar")
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                datos.forEach { (nombre, cantidad) ->
                    val proporcion = cantidad.coerceAtLeast(0).toFloat() / maximo
                    val alturaBarra = (proporcion * 90f).coerceAtLeast(12f).dp

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "$cantidad",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Surface(
                            modifier = Modifier
                                .width(36.dp)
                                .height(alturaBarra),
                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                            color = Color(0xFF2563EB)
                        ) {}
                        Spacer(Modifier.height(8.dp))
                        Text(
                            nombre,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
