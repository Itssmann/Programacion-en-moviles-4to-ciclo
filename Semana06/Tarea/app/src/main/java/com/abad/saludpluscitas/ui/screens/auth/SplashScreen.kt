package com.abad.saludpluscitas.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abad.saludpluscitas.ui.components.BotonSaludPlus

@Composable
fun SplashScreen(
    onContinuar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SaludPlus",
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Tu salud en buenas manos")

        Spacer(modifier = Modifier.height(32.dp))

        BotonSaludPlus(
            texto = "Comenzar",
            onClick = onContinuar
        )
    }
}