package com.abad.saludpluscitas.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.saludpluscitas.ui.components.BotonSaludPlus

@Composable
fun TerminosScreen(
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Términos y condiciones")

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "SaludPlus utiliza los datos registrados para " +
                    "gestionar las citas médicas de los pacientes. " +
                    "Esta aplicación es un proyecto académico."
        )

        Spacer(modifier = Modifier.height(24.dp))

        BotonSaludPlus(
            texto = "Volver",
            onClick = onVolver
        )
    }
}