package com.abad.saludpluscitas.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.saludpluscitas.data.repository.Repositorio
import com.abad.saludpluscitas.ui.components.BotonSaludPlus
import com.abad.saludpluscitas.ui.components.CampoSaludPlus

@Composable
fun LoginScreen(
    onIngresar: () -> Unit,
    onRegistro: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Iniciar sesión")

        Spacer(modifier = Modifier.height(24.dp))

        CampoSaludPlus(
            valor = correo,
            onValorCambio = { correo = it },
            etiqueta = "Correo electrónico"
        )

        Spacer(modifier = Modifier.height(12.dp))

        CampoSaludPlus(
            valor = contrasena,
            onValorCambio = { contrasena = it },
            etiqueta = "Contraseña"
        )

        Spacer(modifier = Modifier.height(16.dp))

        BotonSaludPlus(
            texto = "Ingresar",
            onClick = {
                val usuario = Repositorio.iniciarSesion(
                    correo,
                    contrasena
                )

                if (usuario != null) {
                    onIngresar()
                } else {
                    mensaje = "Correo o contraseña incorrectos"
                }
            }
        )

        if (mensaje.isNotEmpty()) {
            Text(mensaje)
        }

        TextButton(onClick = onRegistro) {
            Text("Crear una cuenta")
        }
    }
}