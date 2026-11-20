package com.abad.campusgps.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.abad.campusgps.model.TipoError
import com.abad.campusgps.model.UbicacionUi
import androidx.compose.ui.Alignment

@Composable
fun MensajeError(
    error: UbicacionUi.Error,
    onReintentar: () -> Unit
) {
    val contexto = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = error.mensaje,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )

        when (error.tipo) {

            TipoError.SIN_PERMISO -> {
                Button(
                    onClick = {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts(
                                "package",
                                contexto.packageName,
                                null)
                        )
                        contexto.startActivity(intent)
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Abrir ajustes")
                }
            }

            TipoError.GPS_APAGADO -> {
                Button(onClick = onReintentar) {
                    Text("Reintentar")
                }
            }

            TipoError.SIN_PLAY_SERVICES -> {
                // No mostrar botón de reintento.
            }

            TipoError.OTRO -> {
                Button(onClick = onReintentar) {
                    Text("Reintentar")
                }
            }
        }
    }
}
