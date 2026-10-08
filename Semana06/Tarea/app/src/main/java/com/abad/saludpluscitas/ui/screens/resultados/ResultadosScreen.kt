package com.abad.saludpluscitas.ui.screens.resultados

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ResultadosScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Resultados médicos",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "No hay resultados disponibles",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Tus resultados médicos aparecerán aquí cuando estén disponibles.")
            }
        }
    }
}

