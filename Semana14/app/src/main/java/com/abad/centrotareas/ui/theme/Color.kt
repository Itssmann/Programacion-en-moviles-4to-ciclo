package com.abad.centrotareas.ui.theme

import androidx.compose.ui.graphics.Color

val VerdePetroleo = Color(0xFF175C56)
val VerdeSecundario = Color(0xFF006B65)
val TurquesaAcento = Color(0xFFB5EDE2)
val FondoGeneral = Color(0xFFF3F6F3)
val ColorTarjeta = Color(0xFFFCFDF9)
val ColorBorde = Color(0xFFC2CECA)
val VerdeExitoFondo = Color(0xFFE0F2E3)
val VerdeExitoTexto = Color(0xFF246237)
val AmbarFondo = Color(0xFFFFEECB)
val AmbarTexto = Color(0xFF795400)
val RojoErrorFondo = Color(0xFFFFDAD6)
val RojoErrorTexto = Color(0xFF963C35)

// Colores semánticos compartidos: el estado nunca se comunica solo con color.
enum class Tono { NEUTRO, ACTIVO, EXITO, CANCELADO, ERROR }

@androidx.compose.runtime.Composable
fun coloresEstado(tono: Tono): Pair<Color, Color> {
    val scheme = androidx.compose.material3.MaterialTheme.colorScheme
    val oscuro = scheme.surface.red < 0.5f
    return when (tono) {
        Tono.NEUTRO -> scheme.surfaceVariant to scheme.onSurfaceVariant
        Tono.ACTIVO -> scheme.primaryContainer to scheme.onPrimaryContainer
        Tono.EXITO -> if (oscuro) Color(0xFF203F2B) to Color(0xFFABE0B6) else VerdeExitoFondo to VerdeExitoTexto
        Tono.CANCELADO -> if (oscuro) Color(0xFF493A19) to Color(0xFFF4D58E) else AmbarFondo to AmbarTexto
        Tono.ERROR -> scheme.errorContainer to scheme.onErrorContainer
    }
}
