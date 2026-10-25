package com.abad.saludpluscitas.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abad.saludpluscitas.data.model.Usuario
import com.abad.saludpluscitas.data.repository.Repositorio
import com.abad.saludpluscitas.ui.components.BotonSaludPlus
import com.abad.saludpluscitas.ui.components.CampoSaludPlus

private val PATRON_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit,
    onTerminos: () -> Unit,
    onLogin: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var aceptaTerminos by rememberSaveable { mutableStateOf(false) }

    var errorNombre by rememberSaveable { mutableStateOf<String?>(null) }
    var errorCorreo by rememberSaveable { mutableStateOf<String?>(null) }
    var errorTelefono by rememberSaveable { mutableStateOf<String?>(null) }
    var errorContrasena by rememberSaveable { mutableStateOf<String?>(null) }
    var errorTerminos by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.LocalHospital,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Crear cuenta",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Regístrate para gestionar tus citas médicas",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        CampoSaludPlus(
            valor = nombre,
            onValorCambio = {
                if (it.all { char -> char.isLetter() || char.isWhitespace() }) {
                    nombre = it
                }
                errorNombre = null
            },
            etiqueta = "Nombre completo",
            icono = Icons.Default.Person,
            esError = errorNombre != null,
            textoError = errorNombre
        )

        Spacer(modifier = Modifier.height(12.dp))

        CampoSaludPlus(
            valor = correo,
            onValorCambio = {
                correo = it
                errorCorreo = null
            },
            etiqueta = "Correo electrónico",
            icono = Icons.Default.Email,
            esError = errorCorreo != null,
            textoError = errorCorreo,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(12.dp))

        CampoSaludPlus(
            valor = telefono,
            onValorCambio = { nuevoTexto ->
                val filtrado = nuevoTexto.filter { it.isDigit() }.take(9)
                telefono = filtrado
                errorTelefono = null
            },
            etiqueta = "Teléfono celular (9 dígitos)",
            icono = Icons.Default.Phone,
            esError = errorTelefono != null,
            textoError = errorTelefono,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(12.dp))

        CampoSaludPlus(
            valor = contrasena,
            onValorCambio = {
                contrasena = it
                errorContrasena = null
            },
            etiqueta = "Contraseña",
            icono = Icons.Default.Lock,
            esError = errorContrasena != null,
            textoError = errorContrasena,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = aceptaTerminos,
                onCheckedChange = {
                    aceptaTerminos = it
                    errorTerminos = null
                }
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Acepto los ",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(
                        onClick = onTerminos,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "términos y condiciones",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (errorTerminos != null) {
            Text(
                text = errorTerminos ?: "",
                color = MaterialTheme.colorScheme.error,
                fontSize = 11.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        BotonSaludPlus(
            texto = "Registrarme",
            onClick = {
                val nombreLimpio = nombre.trim()
                val correoLimpio = correo.trim()
                val telefonoLimpio = telefono.trim()

                errorNombre = when {
                    nombreLimpio.isEmpty() -> "El nombre completo es obligatorio"
                    nombreLimpio.any { it.isDigit() } -> "El nombre no debe contener números"
                    else -> null
                }

                errorCorreo = when {
                    correoLimpio.isEmpty() -> "El correo electrónico es obligatorio"
                    !PATRON_CORREO.matches(correoLimpio) -> "Formato de correo inválido (ej. usuario@gmail.com)"
                    Repositorio.usuarios.any { it.correo.equals(correoLimpio, ignoreCase = true) } -> "El correo electrónico ya está registrado"
                    else -> null
                }

                errorTelefono = when {
                    telefonoLimpio.isEmpty() -> "El teléfono celular es obligatorio"
                    (!telefonoLimpio.startsWith("9") || telefonoLimpio.length != 9) -> "El teléfono debe empezar con 9 y tener 9 dígitos"
                    else -> null
                }

                errorContrasena = when {
                    contrasena.isEmpty() -> "La contraseña es obligatoria"
                    contrasena.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
                    else -> null
                }

                errorTerminos = if (!aceptaTerminos) "Debes aceptar los términos y condiciones" else null

                if (errorNombre == null && errorCorreo == null && errorTelefono == null && errorContrasena == null && errorTerminos == null) {
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

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿Ya tienes cuenta? ",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(
                onClick = onLogin,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Iniciar sesión",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
