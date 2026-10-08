package com.abad.saludpluscitas.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem

@Composable
fun BotonSaludPlus(
    texto: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(texto)
    }
}

@Composable
fun CampoSaludPlus(
    valor: String,
    onValorCambio: (String) -> Unit,
    etiqueta: String
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambio,
        label = { Text(etiqueta) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    titulo: String,
    onVolver: () -> Unit
) {
    TopAppBar(
        title = {
            Text(titulo)
        },
        navigationIcon = {
            IconButton(onClick = onVolver) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        }
    )
}

@Composable
fun BarraInferior(
    rutaActual: String,
    onNavegar: (String) -> Unit
) {
    NavigationBar {

        NavigationBarItem(
            selected = rutaActual == "inicio",
            onClick = { onNavegar("inicio") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )

        NavigationBarItem(
            selected = rutaActual == "misCitas",
            onClick = { onNavegar("misCitas") },
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Citas") },
            label = { Text("Citas") }
        )

        NavigationBarItem(
            selected = rutaActual == "resultados",
            onClick = { onNavegar("resultados") },
            icon = { Icon(Icons.Default.Description, contentDescription = "Resultados") },
            label = { Text("Resultados") }
        )

        NavigationBarItem(
            selected = rutaActual == "perfil",
            onClick = { onNavegar("perfil") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )
    }
}