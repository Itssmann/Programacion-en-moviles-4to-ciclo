package com.abad.huellitasvet.ui.dueno

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.ui.theme.HuellitasVetTheme
import com.abad.huellitasvet.viewmodel.RegistroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    viewModel: RegistroViewModel,
    onRegistroExitoso: () -> Unit,
    onVolverLogin: () -> Unit
) {
    val nombre by viewModel.nombre.collectAsState()
    val correo by viewModel.correo.collectAsState()
    val telefono by viewModel.telefono.collectAsState()
    val distrito by viewModel.distrito.collectAsState()
    val distritoPersonalizado by viewModel.distritoPersonalizado.collectAsState()
    val clave by viewModel.clave.collectAsState()
    val confirmarClave by viewModel.confirmarClave.collectAsState()
    val rol by viewModel.rol.collectAsState()
    val errores by viewModel.errores.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val registroExitoso by viewModel.registroExitoso.collectAsState()
    val mensajeExito by viewModel.mensajeExito.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var expandedDistrito by remember { mutableStateOf(false) }

    val distritosLima = listOf(
        "Miraflores", "San Isidro", "Santiago de Surco", "San Borja",
        "La Molina", "Lince", "Jesús María", "Magdalena",
        "Pueblo Libre", "San Miguel", "Lima Cercado", "Los Olivos",
        "San Martín de Porres", "Comas", "Independencia",
        "San Juan de Lurigancho", "Villa El Salvador", "Villa María del Triunfo",
        "San Juan de Miraflores", "Chorrillos", "El Agustino", "Santa Anita",
        "Ate", "La Victoria", "Rímac", "Breña", "Barranco",
        "San Luis", "Surquillo", "Otros"
    )

    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            showSuccessDialog = true
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("¡Éxito!") },
            text = { Text(mensajeExito ?: "Registro completado con éxito") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    onRegistroExitoso()
                }) {
                    Text("Ir a iniciar sesión")
                }
            }
        )
    }

    HuellitasVetTheme(role = rol) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Crear cuenta",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Regístrate para comenzar en HuellitasVet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(4.dp))

                // Role selector (CLIENTE vs VET)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { viewModel.onRolChange("CLIENTE") },
                            shape = RoundedCornerShape(20.dp),
                            color = if (rol == "CLIENTE") MaterialTheme.colorScheme.primary else Color.Transparent
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "Dueño",
                                    fontWeight = FontWeight.Bold,
                                    color = if (rol == "CLIENTE") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { viewModel.onRolChange("VET") },
                            shape = RoundedCornerShape(20.dp),
                            color = if (rol == "VET") MaterialTheme.colorScheme.primary else Color.Transparent
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "Veterinario",
                                    fontWeight = FontWeight.Bold,
                                    color = if (rol == "VET") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Nombre completo
                OutlinedTextField(
                    value = nombre,
                    onValueChange = viewModel::onNombreChange,
                    label = { Text("Nombre completo") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    isError = errores["nombre"] != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                if (errores["nombre"] != null) {
                    Text(errores["nombre"]!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                // Correo
                OutlinedTextField(
                    value = correo,
                    onValueChange = viewModel::onCorreoChange,
                    label = { Text("Correo electrónico") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = errores["correo"] != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                if (errores["correo"] != null) {
                    Text(errores["correo"]!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                // Teléfono móvil (9 dígitos, empieza con 9)
                OutlinedTextField(
                    value = telefono,
                    onValueChange = viewModel::onTelefonoChange,
                    label = { Text("Teléfono móvil (9 dígitos)") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = errores["telefono"] != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                if (errores["telefono"] != null) {
                    Text(errores["telefono"]!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                // Distrito selector
                ExposedDropdownMenuBox(
                    expanded = expandedDistrito,
                    onExpandedChange = { expandedDistrito = !expandedDistrito },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = distrito,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Distrito de Lima") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDistrito) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDistrito,
                        onDismissRequest = { expandedDistrito = false }
                    ) {
                        distritosLima.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    viewModel.onDistritoChange(opcion)
                                    expandedDistrito = false
                                }
                            )
                        }
                    }
                }

                if (distrito == "Otros") {
                    OutlinedTextField(
                        value = distritoPersonalizado,
                        onValueChange = viewModel::onDistritoPersonalizadoChange,
                        label = { Text("Especificar otro distrito") },
                        isError = errores["distrito"] != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                if (errores["distrito"] != null) {
                    Text(errores["distrito"]!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                // Contraseña
                OutlinedTextField(
                    value = clave,
                    onValueChange = viewModel::onClaveChange,
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = errores["clave"] != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                if (errores["clave"] != null) {
                    Text(errores["clave"]!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                // Confirmar Contraseña
                OutlinedTextField(
                    value = confirmarClave,
                    onValueChange = viewModel::onConfirmarClaveChange,
                    label = { Text("Confirmar contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = errores["confirmarClave"] != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                if (errores["confirmarClave"] != null) {
                    Text(errores["confirmarClave"]!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.registrar() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = !cargando
                ) {
                    if (cargando) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Registrarse", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("¿Ya tienes cuenta?")
                    TextButton(onClick = onVolverLogin) {
                        Text("Inicia sesión", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
