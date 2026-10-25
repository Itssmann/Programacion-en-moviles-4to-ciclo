package com.abad.huellitasvet.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GraficoLineas(
    datos: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    if (datos.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Aún no hay registros de peso suficientes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val minPeso = (datos.minOfOrNull { it.second } ?: 0.0) * 0.9
    val maxPeso = (datos.maxOfOrNull { it.second } ?: 10.0) * 1.1
    val rangoPeso = (maxPeso - minPeso).coerceAtLeast(1.0)

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "Evolución del peso (kg)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                val outlineColor = MaterialTheme.colorScheme.outlineVariant

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val paddingX = 24.dp.toPx()
                    val paddingY = 20.dp.toPx()
                    val chartWidth = width - paddingX * 2
                    val chartHeight = height - paddingY * 2

                    // Horizontal grid lines
                    for (i in 0..2) {
                        val y = paddingY + chartHeight * (i / 2f)
                        drawLine(
                            color = outlineColor.copy(alpha = 0.4f),
                            start = Offset(paddingX, y),
                            end = Offset(width - paddingX, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    if (datos.size == 1) {
                        val x = width / 2f
                        val y = height / 2f
                        drawCircle(
                            color = primaryColor,
                            radius = 6.dp.toPx(),
                            center = Offset(x, y)
                        )
                    } else {
                        val points = datos.mapIndexed { index, (_, peso) ->
                            val x = paddingX + (index.toFloat() / (datos.size - 1).coerceAtLeast(1)) * chartWidth
                            val y = paddingY + chartHeight - ((peso - minPeso) / rangoPeso).toFloat() * chartHeight
                            Offset(x, y)
                        }

                        val path = Path().apply {
                            points.forEachIndexed { index, point ->
                                if (index == 0) moveTo(point.x, point.y)
                                else lineTo(point.x, point.y)
                            }
                        }

                        drawPath(
                            path = path,
                            color = primaryColor,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        points.forEach { point ->
                            drawCircle(
                                color = primaryColor,
                                radius = 5.dp.toPx(),
                                center = point
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 2.dp.toPx(),
                                center = point
                            )
                        }
                    }
                }
            }

            // X-axis dates row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                datos.forEach { (fecha, peso) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${peso}kg",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = fecha,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
