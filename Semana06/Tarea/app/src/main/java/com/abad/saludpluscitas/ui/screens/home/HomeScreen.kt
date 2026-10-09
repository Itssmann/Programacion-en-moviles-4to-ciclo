package com.abad.saludpluscitas.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abad.saludpluscitas.data.repository.Repositorio

@Composable
fun HomeScreen(
    onEspecialidades: () -> Unit,
    onSeleccionarEspecialidad: (Int) -> Unit
) {
    val usuario = Repositorio.usuarioActual
    val nombre = usuario?.nombre?.substringBefore(" ") ?: "Paciente"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "¡Hola, $nombre!",
            fontSize = 26.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text("¿En qué podemos ayudarte hoy?")

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Especialidades destacadas",
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(Repositorio.especialidades) { especialidad ->
                Card(
                    modifier = Modifier
                        .width(170.dp)
                        .clickable {
                            onSeleccionarEspecialidad(especialidad.id)
                        },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(especialidad.nombre)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = especialidad.descripcion,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onEspecialidades,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver todas las especialidades")
        }
    }
}
