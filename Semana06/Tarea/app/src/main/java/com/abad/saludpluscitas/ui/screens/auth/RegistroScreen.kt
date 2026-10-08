package com.abad.saludpluscitas.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            onValorCambio = { nombre = it },
            etiqueta = "Nombre completo",
            icono = Icons.Default.Person
        )

        Spacer(modifier = Modifier.height(12.dp))

        CampoSaludPlus(
            valor = correo,
            onValorCambio = { correo = it },
            etiqueta = "Correo electrónico",
            icono = Icons.Default.Email
        )

        Spacer(modifier = Modifier.height(12.dp))

        CampoSaludPlus(
            valor = telefono,
            onValorCambio = { telefono = it },
            etiqueta = "Teléfono",
            icono = Icons.Default.Phone
        )

        Spacer(modifier = Modifier.height(12.dp))

        CampoSaludPlus(
            valor = contrasena,
            onValorCambio = { contrasena = it },
            etiqueta = "Contraseña",
            icono = Icons.Default.Lock
        )

        Spacer(modifier = Modifier.height(20.dp))

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
            Text(
                text = "Ver términos y condiciones",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
