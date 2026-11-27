package com.abad.avisoscampus.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abad.avisoscampus.R
import com.abad.avisoscampus.model.Origen
import com.abad.avisoscampus.ui.theme.AmbarRecordatorio
import com.abad.avisoscampus.ui.theme.AmbarSuave
import com.abad.avisoscampus.ui.theme.AzulPrincipal
import com.abad.avisoscampus.ui.theme.Bordes
import com.abad.avisoscampus.ui.theme.CelesteSuave
import com.abad.avisoscampus.ui.theme.Turquesa
import com.abad.avisoscampus.ui.theme.TurquesaSuave
import com.abad.avisoscampus.ui.theme.VerdeExito
import com.abad.avisoscampus.ui.theme.VerdeSuave

// Colores e ícono que identifican a cada origen en toda la app
data class EstiloOrigen(@DrawableRes val icono: Int, val color: Color, val fondo: Color)

fun estiloDe(origen: Origen): EstiloOrigen = when (origen) {
    Origen.BOTON -> EstiloOrigen(R.drawable.ic_notificacion, AzulPrincipal, CelesteSuave)
    Origen.RECORDATORIO -> EstiloOrigen(R.drawable.ic_alarma, AmbarRecordatorio, AmbarSuave)
    Origen.MODO_AVION -> EstiloOrigen(R.drawable.ic_avion, Turquesa, TurquesaSuave)
    Origen.CARGADOR -> EstiloOrigen(R.drawable.ic_rayo, VerdeExito, VerdeSuave)
}

@Composable
fun IconoCircular(@DrawableRes icono: Int, color: Color, fondo: Color, tamano: Int = 40) {
    Box(
        modifier = Modifier
            .size(tamano.dp)
            .clip(CircleShape)
            .background(fondo),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icono),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size((tamano * 0.5f).dp)
        )
    }
}

@Composable
fun TarjetaSeccion(
    titulo: String,
    subtitulo: String,
    @DrawableRes icono: Int,
    colorIcono: Color = AzulPrincipal,
    fondoIcono: Color = CelesteSuave,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Bordes),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoCircular(icono, colorIcono, fondoIcono)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        titulo,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        subtitulo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            contenido()
        }
    }
}

@Composable
fun Etiqueta(texto: String, color: Color, fondo: Color, conPunto: Boolean = false) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(fondo)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (conPunto) {
            Box(
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(
            texto,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

// Fila completa tocable con su switch: área táctil amplia y accesible
@Composable
fun FilaInterruptor(
    @DrawableRes icono: Int,
    colorIcono: Color,
    fondoIcono: Color,
    titulo: String,
    descripcion: String,
    activo: Boolean,
    alCambiar: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(14.dp))
            .toggleable(value = activo, role = Role.Switch, onValueChange = alCambiar)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconoCircular(icono, colorIcono, fondoIcono, tamano = 36)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.titleSmall)
            Text(
                descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = activo,
            onCheckedChange = null, // lo maneja toda la fila
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedTrackColor = CelesteSuave,
                uncheckedBorderColor = Bordes,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
