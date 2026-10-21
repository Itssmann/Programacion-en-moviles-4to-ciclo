package com.abad.huellitasvet.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

@Composable
fun MascotaAvatar(
    especie: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(76.dp)) {
        val w = size.width
        val h = size.height
        val gato = especie.uppercase() == "GATO"
        val perro = especie.uppercase() == "PERRO"

        val fondo = when {
            gato -> Color(0xFFFFE8CC)
            perro -> Color(0xFFD1FAE5)
            else -> Color(0xFFDBEAFE)
        }

        drawCircle(fondo, radius = w * 0.49f)

        val cara = when {
            gato -> Color(0xFFF59E0B)
            perro -> Color(0xFFD6A36A)
            else -> Color(0xFF93C5FD)
        }

        if (gato) {
            val izquierda = Path().apply {
                moveTo(w * .20f, h * .38f)
                lineTo(w * .25f, h * .09f)
                lineTo(w * .47f, h * .29f)
                close()
            }
            val derecha = Path().apply {
                moveTo(w * .53f, h * .29f)
                lineTo(w * .75f, h * .09f)
                lineTo(w * .80f, h * .38f)
                close()
            }
            drawPath(izquierda, cara)
            drawPath(derecha, cara)
        } else if (perro) {
            drawOval(
                color = Color(0xFF9A623B),
                topLeft = Offset(w * .12f, h * .26f),
                size = androidx.compose.ui.geometry.Size(w * .23f, h * .40f)
            )
            drawOval(
                color = Color(0xFF9A623B),
                topLeft = Offset(w * .65f, h * .26f),
                size = androidx.compose.ui.geometry.Size(w * .23f, h * .40f)
            )
        }

        drawCircle(
            color = cara,
            radius = w * .31f,
            center = Offset(w * .50f, h * .51f)
        )

        val ojos = Color(0xFF1F2937)

        drawCircle(
            color = ojos,
            radius = w * .035f,
            center = Offset(w * .39f, h * .47f)
        )
        drawCircle(
            color = ojos,
            radius = w * .035f,
            center = Offset(w * .61f, h * .47f)
        )

        drawCircle(
            color = Color(0xFF78350F),
            radius = w * .048f,
            center = Offset(w * .50f, h * .59f)
        )
    }
}
