package com.abad.huellitasvet.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GraficoDona(
    datos: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    val colores = listOf(
        Color(0xFFD97706),
        Color(0xFF16A34A),
        Color(0xFF475569),
        Color(0xFFDC2626),
        Color(0xFF2563EB)
    )

    val total = datos.sumOf { it.second.coerceAtLeast(0) }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.size(130.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val grosor = 20.dp.toPx()
                    val margen = grosor / 2f
                    val diametro = size.minDimension - grosor

                    if (total == 0) {
                        drawArc(
                            color = Color.LightGray,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = Offset(margen, margen),
                            size = Size(diametro, diametro),
                            style = Stroke(grosor)
                        )
                    } else {
                        var inicio = -90f

                        datos.forEachIndexed { indice, (_, cantidad) ->
                            val barrido =
                                cantidad.coerceAtLeast(0).toFloat() / total * 360f

                            if (barrido > 0f) {
                                drawArc(
                                    color = colores[indice % colores.size],
                                    startAngle = inicio,
                                    sweepAngle = barrido,
                                    useCenter = false,
                                    topLeft = Offset(margen, margen),
                                    size = Size(diametro, diametro),
                                    style = Stroke(
                                        width = grosor,
                                        cap = StrokeCap.Butt
                                    )
                                )
                            }

                            inicio += barrido
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$total",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text("citas", style = MaterialTheme.typography.bodySmall)
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                datos.forEachIndexed { indice, (nombre, cantidad) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(10.dp),
                            shape = CircleShape,
                            color = colores[indice % colores.size]
                        ) {}
                        Text(
                            nombre,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.width(80.dp)
                        )
                        Text(
                            "$cantidad",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
