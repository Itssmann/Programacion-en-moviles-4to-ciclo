package com.abad.saludpluscitas.ui.screens.agendamiento

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.saludpluscitas.ui.components.BotonSaludPlus

@Composable
fun CitaExitosaScreen(
    onVerCitas: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¡Cita registrada!",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Tu cita médica se registró correctamente.")

        Spacer(modifier = Modifier.height(32.dp))

        BotonSaludPlus(
            texto = "Ver mis citas",
            onClick = onVerCitas
        )
    }
}