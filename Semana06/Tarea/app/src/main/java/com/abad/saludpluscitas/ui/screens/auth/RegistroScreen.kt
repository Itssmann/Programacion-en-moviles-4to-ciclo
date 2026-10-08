package com.abad.saludpluscitas.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.saludpluscitas.data.model.Usuario
import com.abad.saludpluscitas.data.repository.Repositorio
import com.abad.saludpluscitas.ui.components.BotonSaludPlus
import com.abad.saludpluscitas.ui.components.CampoSaludPlus

@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit,
    onTerminos: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Crear cuenta")

        Spacer(modifier = Modifier.height(16.dp))

        CampoSaludPlus(
            valor = nombre,
            onValorCambio = { nombre = it },
            etiqueta = "Nombre completo"
        )

        CampoSaludPlus(
            valor = correo,
            onValorCambio = { correo = it },
            etiqueta = "Correo electrónico"
        )

        CampoSaludPlus(
            valor = telefono,
            onValorCambio = { telefono = it },
            etiqueta = "Teléfono"
        )

        CampoSaludPlus(
            valor = contrasena,
            onValorCambio = { contrasena = it },
            etiqueta = "Contraseña"
        )

        Spacer(modifier = Modifier.height(16.dp))

        BotonSaludPlus(
            texto = "Registrarme",
            onClick = {
                if (
                    nombre.isNotBlank() &&
                    correo.isNotBlank() &&
                    telefono.isNotBlank() &&
                    contrasena.isNotBlank()
                ) {
                    val nuevoUsuario = Usuario(
                        id = (Repositorio.usuarios.maxOfOrNull { it.id } ?: 0) + 1,
                        nombre = nombre,
                        correo = correo,
                        telefono = telefono,
                        contrasena = contrasena
                    )

                    Repositorio.registrarUsuario(nuevoUsuario)
                    onRegistroExitoso()

                } else {
                    mensaje = "Completa todos los campos"
                }
            }
        )
        TextButton(
            onClick = onTerminos
        ) {
            Text("Ver términos y condiciones")
        }

        if (mensaje.isNotEmpty()) {
            Text(mensaje)
        }
    }
}