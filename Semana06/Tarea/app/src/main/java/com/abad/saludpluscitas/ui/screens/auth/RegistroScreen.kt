package com.abad.saludpluscitas.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    var aceptaTerminos by remember { mutableStateOf(false) }
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

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = aceptaTerminos,
                onCheckedChange = { aceptaTerminos = it }
            )

            TextButton(
                onClick = onTerminos
            ) {
                Text("Acepto los términos y condiciones")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        BotonSaludPlus(
            texto = "Registrarme",
            onClick = {
                val nombreLimpio = nombre.trim()
                val correoLimpio = correo.trim()
                val telefonoLimpio = telefono.trim()
                mensaje = when {
                    nombreLimpio.isBlank() ||
                            correoLimpio.isBlank() ||
                            telefonoLimpio.isBlank() ||
                            contrasena.isBlank() ->
                        "Completa todos los campos"

                    !Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches() ->
                        "Ingresa un correo electrónico válido"

                    !telefonoLimpio.matches(Regex("^9[0-9]{8}$")) ->
                        "El celular debe tener 9 dígitos y empezar con 9"

                    contrasena.length < 6 ->
                        "La contraseña debe tener al menos 6 caracteres"

                    Repositorio.usuarios.any {
                        it.correo.equals(correoLimpio, ignoreCase = true)
                    } ->
                        "Este correo electrónico ya está registrado"

                    !aceptaTerminos ->
                        "Debes aceptar los términos y condiciones"

                    else -> ""
                }

                if (mensaje.isEmpty()) {
                    val nuevoUsuario = Usuario(
                        id = (Repositorio.usuarios.maxOfOrNull { it.id } ?: 0) + 1,
                        nombre = nombreLimpio,
                        correo = correoLimpio,
                        telefono = telefonoLimpio,
                        contrasena = contrasena
                    )
                    Repositorio.registrarUsuario(nuevoUsuario)
                    onRegistroExitoso()
                }
            }
        )
        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
